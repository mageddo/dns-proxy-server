package main

import (
	_ "github.com/mageddo/dns-proxy-server/log"
	_ "github.com/mageddo/dns-proxy-server/controller"
	"fmt"
	"os"
	"runtime/pprof"
	"strings"
	"github.com/miekg/dns"
	"github.com/mageddo/dns-proxy-server/proxy"
	"reflect"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/events/docker"
	"net/http"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/utils/exitcodes"
	"github.com/mageddo/dns-proxy-server/service"
	"github.com/mageddo/go-logging"
	"runtime/debug"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/resolvconf"
	"context"
	"time"
)

func handleQuestion(respWriter dns.ResponseWriter, reqMsg *dns.Msg) {

	defer func() {
		err := recover()
		if err != nil {
			logging.Errorf("status=error, error=%v, stack=%s", err, string(debug.Stack()))
		}
	}()

	var firstQuestion dns.Question
	questionsQtd := len(reqMsg.Question)
	if questionsQtd != 0 {
		firstQuestion = reqMsg.Question[0]
	}else{
		logging.Error("status=question-is-nil")
		return
	}

	logging.Debugf("status=begin, reqId=%d, questions=%d, question=%s, type=%s", reqMsg.Id,
	questionsQtd, firstQuestion.Name, utils.DnsQTypeCodeToName(firstQuestion.Qtype))

	// loading the solvers and try to solve the hostname in that order
	solvers := []proxy.DnsSolver{
		proxy.NewSystemSolver(), proxy.NewDockerSolver(docker.GetCache()),
		proxy.NewLocalDNSSolver(store.GetInstance()), proxy.NewRemoteDnsSolver(),
	}
	
	for _, solver := range solvers {

		solverID := reflect.TypeOf(solver).String()
		logging.Debugf("status=begin, solver=%s", solverID)
		// loop through questions
		resp, err := solver.Solve(context.Background(), firstQuestion)
		if resp != nil {

			var firstAnswer dns.RR
			answerLenth := len(resp.Answer)

			logging.Debugf("status=answer-found, solver=%s, length=%d", solverID, answerLenth)
			if answerLenth != 0 {
				firstAnswer = resp.Answer[0]
			}
			logging.Debugf("status=resolved, solver=%s, alength=%d, answer=%v", solverID, answerLenth, firstAnswer)

			resp.SetReply(reqMsg)
			resp.Compress = conf.Compress()
			respWriter.WriteMsg(resp)
			break
		}
		logging.Debugf("status=not-resolved, solver=%s, err=%v", solverID, err)

	}

}

func serve(net, name, secret string) {
	port := fmt.Sprintf(":%d", conf.DnsServerPort())
	logging.Info("status=begin, port=%d", conf.DnsServerPort())
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
		logging.Infof("status=setup-default-dns, setup-dns=%b", conf.SetupResolvConf())
		if conf.SetupResolvConf() {
			for ; ; {
				logging.Info("status=updating-resolv.conf")
				err := resolvconf.SetCurrentDNSServerToMachine()
				if err != nil {
					logging.Error("status=cant-turn-default-dns", err)
					exitcodes.Exit(exitcodes.FAIL_SET_DNS_AS_DEFAULT)
				}
				time.Sleep(time.Duration(20) * time.Second)
			}
		}
	}()

	logging.Warningf("server started")
	s := <- utils.Sig
	logging.Warningf("status=exiting ;) signal=%s", s)
	resolvconf.RestoreResolvconfToDefault()
}
