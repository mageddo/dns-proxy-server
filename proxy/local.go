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

func getCnameMsg(question dns.Question, hostname *local.HostnameVo) *dns.Msg{
	rr := &dns.CNAME{
		Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeCNAME, Class: 256, Ttl: 60},
		Target: hostname.Target + ".",
	}
	m := new(dns.Msg)
	m.Answer = append(m.Answer, rr)
	return m
}

func getAMsg(question dns.Question, hostname *local.HostnameVo) *dns.Msg{
	rr := &dns.A{
		Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 60},
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
		return nil, errors.New("Not active env found")
	}

	if hostname, _ := activeEnv.GetHostname(key); hostname != nil {
		switch hostname.Type {
		case local.A, "":
			return getAMsg(question, hostname), nil
		case local.CNAME:
			//targetHostname, i := activeEnv.GetHostname(hostname.Target)
			//if i < 0 {
			//	return nil, errors.New("CNAME Target not found for hostname " + question.Name)
			//}
			return getCnameMsg(question, hostname), nil
		}
	}
	return nil, errors.New("hostname not found " + key)
}
