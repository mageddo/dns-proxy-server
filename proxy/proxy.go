package proxy

import (
	"github.com/miekg/dns"
	"golang.org/x/net/context"
)

type DnsSolver interface {
	Solve(ctx context.Context, question dns.Question) (*dns.Msg, error)
}
