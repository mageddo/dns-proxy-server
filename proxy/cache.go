package proxy

import (
	"context"
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/cache/timed"
	"github.com/miekg/dns"
)

type CacheDnsSolver struct {
	c cache.Cache
	decorator DnsSolver
}

func (s CacheDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	hostname := fmt.Sprintf("%s-%d", question.Name, question.Qtype)
	if r := s.c.Get(hostname); r != nil {
		return r.(*dns.Msg), nil
	}
	msg, err := s.decorator.Solve(ctx, question)
	if err != nil {
		return nil, err
	}
	s.c.Put(hostname, msg)
	return msg, nil
}

func NewCacheDnsSolver(decorator DnsSolver) DnsSolver {
	return &CacheDnsSolver{timed.New(lru.New(2048), 30), decorator}
}
