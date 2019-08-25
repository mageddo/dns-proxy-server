package proxy

import (
	"context"
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/cache/timed"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"github.com/pkg/errors"
	"reflect"
	"time"
)

type CacheDnsSolver struct {
	c        *timed.TimedCache
	delegate DnsSolver
}

func (s CacheDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	hostname := getHostnameKey(question)
	if msg, err := s.doSolve(ctx, hostname); err == nil {
		return msg, nil
	}
	logging.Debugf("status=delegating, delegate=%s, host=%s", ctx, s.delegate.Name(), hostname)
	msg, err := s.delegate.Solve(ctx, question)
	if err != nil {
		return msg, err
	}

	ttl := int64(getTtl(msg))
	s.c.PutTTL(hostname, msg, ttl)
	logging.Infof(
		"status=storing-in-the-cache, delegate=%s, host=%s, ttl-seconds=%d, answers=%d",
		ctx, s.delegate.Name(), hostname, ttl, len(msg.Answer),
	)
	return msg, nil
}


func (s CacheDnsSolver) Name() string {
	return reflect.TypeOf(s).String()
}

func (s CacheDnsSolver) doSolve(ctx context.Context, hostname string) (*dns.Msg, error) {
	if r := s.c.GetTimeValue(hostname); r != nil {
		tvalue := r.(timed.TimedValue)
		msg := tvalue.Value().(*dns.Msg).Copy()
		ttl := getTtl(msg)
		leftTime := (time.Duration(ttl) * time.Second) - time.Now().Sub(tvalue.Creation())
		for _, v := range msg.Answer {
			v.Header().Ttl = uint32(leftTime / time.Second)
		}
		logging.Debugf(
			"status=returning-cached-answer, delegate=%s, host=%s, seconds=%d, leftTime=%s",
			ctx, s.delegate.Name(), hostname, ttl, leftTime,
		)
		return msg, nil
	}
	return nil, errors.New(fmt.Sprintf("%s not found in the cache", hostname))
}

func getTtl(msg *dns.Msg) uint32 {
	for _, answer := range msg.Answer {
		return uint32(answer.Header().Ttl)
	}
	return 5
}

func getHostnameKey(question dns.Question) string {
	return fmt.Sprintf("%s-%d", question.Name, question.Qtype)
}

func NewCacheDnsSolver(delegate DnsSolver) DnsSolver {
	cache := store.RegisterCache(timed.New(lru.New(4096), 30)).(*timed.TimedCache)
	return &CacheDnsSolver{cache, delegate}
}
