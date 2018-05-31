package proxy

import (
	"errors"
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/timed"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/miekg/dns"
	"golang.org/x/net/context"
	"net"
	"strings"
	"time"
	"github.com/mageddo/go-logging"
	"fmt"
)

type localDnsSolver struct {
	Cache cache.Cache
}

func (s localDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {

	questionName := question.Name[:len(question.Name)-1]

	// simple solving
	var key = questionName
	if msg, err := s.solveHostname(ctx, question, key); err == nil {
		return msg, nil
	}

	// solving domain by wild card
	key = fmt.Sprintf(".%s", questionName)
	if msg, err := s.solveHostname(ctx, question, key); err == nil {
		return msg, nil
	}

	// Solving subdomains by wildcard
	i := strings.Index(questionName, ".")
	if i > 0 {
		key = questionName[i:]
		if msg, err := s.solveHostname(ctx, question, key); err == nil {
			return msg, nil
		}
	}
	return nil, errors.New("hostname not found " + key)
}

func NewLocalDNSSolver(c cache.Cache) *localDnsSolver {
	return &localDnsSolver{c}
}

func (s localDnsSolver) ContainsKey(key interface{}) (interface{}, bool) {
	if !s.Cache.ContainsKey(key) {
		logging.Debugf("status=notfound, key=%v", key)
		return nil, false
	}
	if v := s.Cache.Get(key).(timed.TimedValue); v.IsValid(time.Now()) {
		logging.Debugf("status=fromcache, key=%v", key)
		return v.Value(), true
	}
	logging.Debugf("status=expired, key=%v", key)
	s.Cache.Remove(key)
	return nil, false
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
	if value, found := s.ContainsKey(key); found {
		logging.Debugf("solver=local, status=from-cache, hostname=%s, value=%v", key, value)
		hostname := value.(*local.HostnameVo)
		if hostname != nil {
			return s.getMsg(question, hostname), nil
		}
	}

	logging.Debugf("solver=local, status=hot-load, hostname=%s", key)
	conf, err := local.LoadConfiguration()
	if err != nil {
		logging.Errorf("status=could-not-load-conf, err=%v", err)
		return nil, err
	}
	activeEnv, _ := conf.GetActiveEnv()
	if activeEnv == nil {
		return nil, errors.New("original env")
	}
	var ttl int64 = 86400 // 24 hours
	hostname, _ := activeEnv.GetHostname(key)
	if hostname != nil {
		ttl = int64(hostname.Ttl)
	}
	val := s.Cache.PutIfAbsent(key, timed.NewTimedValue(hostname, time.Now(), time.Duration(ttl)*time.Second))
	logging.Debugf("status=put, key=%s, value=%v, ttl=%d", key, val, ttl)
	if hostname != nil {
		return s.getMsg(question, hostname), nil
	}
	return nil, errors.New("hostname not found " + key)
}
