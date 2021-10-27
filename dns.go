package main

import (
	_ "github.com/mageddo/dns-proxy-server/log"
	_ "github.com/mageddo/dns-proxy-server/controller/v1"
	"fmt"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/events/docker"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/proxy"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/dns-proxy-server/resolvconf"
	"github.com/mageddo/dns-proxy-server/service"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/dns-proxy-server/utils/exitcodes"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"net/http"
	"os"
	"runtime/debug"
	"runtime/pprof"
	"strings"
	"sync/atomic"
	"time"
)

func handleQuestion(respWriter dns.ResponseWriter, reqMsg *dns.Msg) {
	ctx := reference.Context()
	defer func() {
		err := recover()
		if err != nil {
			logging.Errorf("status=fatal-error-handling-question, req=%+v, err=%s, stack=%+v", ctx, reqMsg.Question, err, string(debug.Stack()))
		}
	}()

	var firstQuestion dns.Question
	questionsQtd := len(reqMsg.Question)
	if questionsQtd != 0 {
		firstQuestion = reqMsg.Question[0]
	} else {
		logging.Error(ctx, "status=no-questions-to-answer, reqId=%d", reqMsg.Id)
		return
	}

	logging.Debugf(
		"status=begin, reqId=%d, questions=%d, question=%s, type=%s", ctx, reqMsg.Id,
		questionsQtd, firstQuestion.Name, utils.DnsQTypeCodeToName(firstQuestion.Qtype),
	)

	solverFactory := proxy.NewCnameDnsSolverFactory(&proxy.DefaultDnsSolverFactory{})
	if msg, err := solverFactory.Solve(ctx, firstQuestion, getSolvers()); msg != nil {
		rcode := msg.Rcode
		logging.Debugf("status=writing-res, code=%d, question=%+v, answers=%+v", ctx, rcode, firstQuestion, getAnswer(msg))
		msg.SetReply(reqMsg)
		msg.Compress = conf.Compress()
		msg.Rcode = rcode
		respWriter.WriteMsg(msg)
	} else {
		logging.Errorf("status=complete, question=%+v, answers=%+v", ctx, firstQuestion, getAnswer(msg), err)
	}
}

func getAnswer(msg *dns.Msg) []dns.RR {
	if msg == nil {
		return nil
	}
	return msg.Answer
}

var solversCreated int32 = 0
var solvers []proxy.DnsSolver = nil
func getSolvers() []proxy.DnsSolver {
	if atomic.CompareAndSwapInt32(&solversCreated, 0, 1) {
		// loading the solvers and try to solve the hostname in that order
		solvers = []proxy.DnsSolver{
			proxy.NewSystemSolver(), proxy.NewDockerSolver(docker.GetCache()),
			proxy.NewCacheDnsSolver(proxy.NewLocalDNSSolver()), proxy.NewCacheDnsSolver(proxy.NewRemoteDnsSolver()),
		}
	}
	return solvers
}

func serve(net, name, secret string) {
	port := fmt.Sprintf(":%d", conf.DnsServerPort())
	logging.Infof("status=begin, port=%d", conf.DnsServerPort())
	switch name {
	case "":
		server := &dns.Server{Addr: port, Net: net, TsigSecret: nil}
		if err := server.ListenAndServe(); err != nil {
			logging.Infof("Failed to setup the %s server", net, err)
			exitcodes.Exit(exitcodes.FAIL_START_DNS_SERVER)
		}
	default:
		server := &dns.Server{Addr: port, Net: net, TsigSecret: map[string]string{name: secret}}
		if err := server.ListenAndServe(); err != nil {
			logging.Infof("Failed to setup the %s server", net, err)
			exitcodes.Exit(exitcodes.FAIL_START_DNS_SERVER)
		}
	}
}

func main() {

	service.NewService().Install()

	var name, secret string
	if conf.Tsig() != "" {
		a := strings.SplitN(conf.Tsig(), ":", 2)
		name, secret = dns.Fqdn(a[0]), a[1] // fqdn the name, which everybody forgets...
	}
	if conf.CpuProfile() != "" {
		f, err := os.Create(conf.CpuProfile())
		if err != nil {
			logging.Error(err)
			os.Exit(-3)
		}
		pprof.StartCPUProfile(f)
		defer pprof.StopCPUProfile()
	}

	dns.HandleFunc(".", handleQuestion)

	local.LoadConfiguration()

	// listen docker container events
	go docker.HandleDockerEvents()

	// start server
	go serve("tcp", name, secret)
	go serve("udp", name, secret)

	// start web server and Rest API
	go func(){
		webPort := conf.WebServerPort()
		logging.Infof("status=web-server-starting, port=%d", webPort)
		if err := http.ListenAndServe(fmt.Sprintf(":%d", webPort), nil); err != nil {
			logging.Errorf("status=web-server-start-failed, err=%v, port=%d", err, webPort)
			exitcodes.Exit(exitcodes.FAIL_START_WEB_SERVER)
		}else{
			logging.Infof("status=web-server-started, port=%d", webPort)
		}
	}()

	// setup resolv conf
	go func() {
		ctx := reference.Context()
		logging.Infof("status=setup-default-dns, setup-dns=%t", ctx, conf.SetupResolvConf())
		if conf.SetupResolvConf() {
			for ; ; {
				if err := resolvconf.SetCurrentDnsServerToMachine(ctx); err != nil {
					logging.Error("status=cant-turn-default-dns", err)
					exitcodes.Exit(exitcodes.FAIL_SET_DNS_AS_DEFAULT)
				}
				time.Sleep(time.Duration(conf.GetResolvSetupSleepSecond()) * time.Second)
			}
		}
	}()

	logging.Warningf("server started")
	s := <- utils.Sig
	logging.Warningf("status=exiting ;) signal=%s", s)
	resolvconf.RestoreResolvconfToDefault()
}
