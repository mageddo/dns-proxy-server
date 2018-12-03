package proxy

import (
	"errors"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"golang.org/x/net/context"
	"net"
)

type localDnsSolver struct {
}

func (s localDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	questionName := question.Name[:len(question.Name)-1]
	for _, host := range getAllHosts("." + questionName) {
		if msg, err := s.solveHostname(ctx, question, host); err == nil {
			return msg, nil
		}
	}
	return nil, errors.New("hostname not found " + questionName)
}

func NewLocalDNSSolver() *localDnsSolver {
	return &localDnsSolver{}
}

func (*localDnsSolver) getMsg(question dns.Question, hostname *local.HostnameVo) *dns.Msg {
	rr := &dns.A{
		Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 0},
		A:   net.IPv4(hostname.Ip[0], hostname.Ip[1], hostname.Ip[2], hostname.Ip[3]),
	}
	m := new(dns.Msg)
	m.Answer = append(m.Answer, rr)
	return m
}

func (s localDnsSolver) solveHostname(ctx context.Context, question dns.Question, key string) (*dns.Msg, error) {
	logging.Debugf("solver=local, status=hot-load, hostname=%s", ctx, key)
	conf, err := local.LoadConfiguration()
	if err != nil {
		logging.Errorf("status=could-not-load-conf, err=%v", ctx, err)
		return nil, err
	}
	activeEnv, _ := conf.GetActiveEnv()
	if activeEnv == nil {
		return nil, errors.New("original env")
	}
	hostname, _ := activeEnv.GetHostname(key)
	if hostname != nil {
		return s.getMsg(question, hostname), nil
	}
	return nil, errors.New("hostname not found " + key)
}
