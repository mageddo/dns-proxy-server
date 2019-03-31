package proxy

import (
	"context"
	"errors"
	"github.com/miekg/dns"
)

type DnsSolverFactory interface {
	Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error)
}

type DefaultDnsSolverFactory struct {

}

func (*DefaultDnsSolverFactory) Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error) {
	for _, solver := range solvers {
		msg, err := solver.Solve(ctx, question)
		if msg != nil {
			return msg, err
		}
	}
	return nil, errors.New("Not solver for the question " + question.Name)
}

type CnameDnsSolverFactory struct {
	proxy DnsSolverFactory
}
func NewCnameDnsSolverFactory(delegate DnsSolverFactory) CnameDnsSolverFactory {
	return CnameDnsSolverFactory{proxy:delegate}
}

func (s *CnameDnsSolverFactory) Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error) {

	firstMsg, err := s.proxy.Solve(ctx, question, solvers)
	if err != nil {
		return nil, err
	}
	firstAnswer := firstMsg.Answer[0]
	if firstAnswer.Header().Rrtype == dns.TypeCNAME && firstAnswer.Header().Class == 256 {
		question.Name = firstAnswer.(*dns.CNAME).Target
		if secondMsg, err := s.proxy.Solve(ctx, question, solvers); secondMsg != nil {
			if err != nil {
				return nil, err
			}
			answers := []dns.RR {
				&dns.CNAME{
					Hdr: dns.RR_Header{Name: firstAnswer.Header().Name, Rrtype: dns.TypeCNAME, Class: dns.ClassINET, Ttl:  firstAnswer.Header().Ttl},
					Target: firstAnswer.(*dns.CNAME).Target,
				},
			}
			m := new(dns.Msg)
			m.Answer = append(answers, secondMsg.Answer...)
			return m, err
		}
	}
	return firstMsg, err
}
