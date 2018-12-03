package proxy

import (
	"github.com/miekg/dns"
	"golang.org/x/net/context"
	"strings"
)

type DnsSolver interface {
	Solve(ctx context.Context, question dns.Question) (*dns.Msg, error)
}

func getAllHosts(hostname string) []string {
	hostnames := []string{hostname[1:]}
	var fromIndex, actual = 0, 0
	for ; ; {
		str := hostname[fromIndex:]
		actual = strings.Index(str, ".")

		if actual == -1 || actual + 1 >= len(str) {
			break
		}
		hostnames = append(hostnames, str[actual:])
		fromIndex += actual + 1
	}
	return hostnames
}
