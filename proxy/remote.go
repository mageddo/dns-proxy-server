package proxy

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"github.com/miekg/dns"
	"net"
	"errors"
	"fmt"
	"golang.org/x/net/context"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/go-logging"
	"reflect"
	"strconv"

	"github.com/mageddo/dns-proxy-server/cache/store"
)

const SERVERS = "SERVERS"

type remoteDnsSolver struct {
	confloader func(ctx context.Context) (*localvo.Configuration, error)
}

// reference https://miek.nl/2014/August/16/go-dns-package/
func (r remoteDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	c := store.GetInstance()
	client := new(dns.Client)
	m := new(dns.Msg)
	m.SetQuestion(dns.Fqdn(question.Name), question.Qtype) // CAN BE A, AAA, MX, etc.
	m.RecursionDesired = true

	logging.Debugf("solver=remote, status=solving, name=%s, qtype=%d", ctx, question.Name, question.Qtype)
	var config *localvo.Configuration
	var err error
	if !c.ContainsKey(SERVERS) {
		if config, err = r.confloader(ctx); err != nil {
			logging.Errorf("error=%v", err)
			return nil, err
		}
		c.PutIfAbsent(SERVERS, config)
	}
	config = c.Get(SERVERS).(*localvo.Configuration)

	var res *dns.Msg
	for _, server := range config.GetRemoteServers(ctx) {

		// server and port to ask
		res, _, err = client.Exchange(m, net.JoinHostPort(server.Ip, strconv.Itoa(server.Port)))

		// if the answer not be returned
		if res == nil {
			err = errors.New(fmt.Sprintf("status=answer-can-not-be-null, err=%v", err))
			logging.Infof("status=no-answer, question=%s, server=%s, err=%s", ctx, question.Name, server.Ip, err)
			continue
		} else if res.Rcode != dns.RcodeSuccess { // what the code of the return message ?
			err = errors.New(fmt.Sprintf("status=invalid-answer-name, name=%s, rcode=%d", question.Name, res.Rcode))
			logging.Infof("status=bad-code, name=%s, rcode=%d, err=%s", ctx, question.Name, res.Rcode, err.Error())
			continue
		}
		logging.Debugf("status=remote-solved, server=%s, name=%s, res=%d, answers=%d", ctx, server.Ip, question.Name, getRCode(res), len(res.Answer))
		return res, nil
	}
	logging.Infof("status=complete, name=%s, res=%d", ctx, question.Name, getRCode(res))
	return res, err
}

func (r remoteDnsSolver) Name() string {
	return reflect.TypeOf(r).String()
}

func NewRemoteDnsSolver() *remoteDnsSolver {
	return &remoteDnsSolver{
		confloader: func(ctx context.Context) (*localvo.Configuration, error) {
			return local.LoadConfiguration()
		},
	}
}

func getRCode(msg *dns.Msg) int {
	if msg == nil {
		return -1
	}
	return msg.Rcode
}
