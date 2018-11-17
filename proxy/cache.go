package proxy

import (
	"context"
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/cache/timed"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"time"
)

type CacheDnsSolver struct {
	c *timed.TimedCache
	decorator DnsSolver
}

func (s CacheDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	hostname := fmt.Sprintf("%s-%d", question.Name, question.Qtype)
	if r := s.c.GetTimeValue(hostname); r != nil {
		tvalue := r.(timed.TimedValue)
		msg := tvalue.Value().(*dns.Msg).Copy()
		ttl := msg.Answer[0].Header().Ttl
		if ttl <= 0 {
			ttl = 5
		}
		leftTime := (time.Duration(ttl) * time.Second) - time.Now().Sub(tvalue.Creation())
		for _, v := range msg.Answer {
			v.Header().Ttl = uint32(leftTime / time.Second)
		}
		logging.Debugf("status=cached-answer, host=%s, seconds=%d, leftTime=%s", ctx, hostname, ttl, leftTime)
		return msg, nil
	}
	msg, err := s.decorator.Solve(ctx, question)
	if err != nil {
		return nil, err
	}
	for _, answer := range msg.Answer {
		ttl := int64(answer.Header().Ttl)
		s.c.PutTTL(hostname, msg, ttl)
		logging.Infof("status=caching, host=%s, seconds=%d", ctx, hostname, ttl)
		break
	}
	return msg, nil
}

func NewCacheDnsSolver(decorator DnsSolver) DnsSolver {
	return &CacheDnsSolver{timed.New(lru.New(2048), 30), decorator}
}
