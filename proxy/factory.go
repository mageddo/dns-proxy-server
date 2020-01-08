package proxy

import (
	"context"
	"errors"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
)

type DnsSolverFactory interface {
	Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error)
}

type DefaultDnsSolverFactory struct {}

func (*DefaultDnsSolverFactory) Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error) {
	var solver DnsSolver
	for _, solver = range solvers {
		if msg, err := solver.Solve(ctx, question); msg != nil {
			logging.Debugf(
				"solver=%s, status=found, question=%+v, answers=%d",
				ctx, getSolverName(solver), question, len(msg.Answer),
			)
			return msg, err
		}
	}
	logging.Debugf("status=not-found, lastSolver=%s, question=%+v", ctx, getSolverName(solver), question)
	return nil, errors.New("Not solver for the question " + question.Name)
}

func getSolverName(solver DnsSolver) string {
	if solver == nil {
		return ""
	}
	return solver.Name()
}

type CnameDnsSolverFactory struct {
	proxy DnsSolverFactory
}

func NewCnameDnsSolverFactory(delegate DnsSolverFactory) CnameDnsSolverFactory {
	return CnameDnsSolverFactory{proxy:delegate}
}

func (s *CnameDnsSolverFactory) Solve(ctx context.Context, question dns.Question, solvers []DnsSolver) (*dns.Msg, error) {

	firstMsg, err := s.proxy.Solve(ctx, question, solvers)
	if err != nil || len(firstMsg.Answer) == 0 {
		return firstMsg, err
	}

	firstAnswer := firstMsg.Answer[0]
	if firstAnswer.Header().Rrtype == dns.TypeCNAME && firstAnswer.Header().Class == 256 {
		question.Name = firstAnswer.(*dns.CNAME).Target
		logging.Debugf("status=solving-cname, questionName=%s", ctx, question.Name)
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
