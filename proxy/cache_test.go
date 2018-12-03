package proxy

import (
	"context"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/miekg/dns"
	"github.com/pkg/errors"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"net"
	"testing"
)

var testCtx = reference.Context()

func TestMustCacheWhenResultIsSuccess(t *testing.T){

	// arrange
	c := &FakeSolver{}
	solver := NewCacheDnsSolver(c)
	q := dns.Question{Name: "acme.com."}

	rr := &dns.A{
		Hdr: dns.RR_Header{Name: "acme.com", Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 5},
		A:   net.IPv4(81, 105, 200, 12),
	}
	m := new(dns.Msg)
	m.Answer = append(m.Answer, rr)

	c.On("Solve", testCtx, q).Return(m, nil)

	for i := 0; i < 2; i++ {
		// act
		msg, err := solver.Solve(testCtx, q)

		// assert
		assert.Nil(t, err)
		assert.NotNil(t, msg)
	}

	c.AssertNumberOfCalls(t, "Solve", 1)
}

func TestMustNotCacheWhenResultIsError(t *testing.T){

	// arrange
	c := &FakeSolver{}
	solver := NewCacheDnsSolver(c)
	q := dns.Question{Name: "acme.com."}
	c.On("Solve", testCtx, q).Return(nil, errors.New("not found"))

	for i := 0; i < 2; i++ {
		// act
		msg, err := solver.Solve(testCtx, q)

		// assert
		assert.NotNil(t, err)
		assert.Nil(t, msg)
	}

	c.AssertNumberOfCalls(t, "Solve", 2)
}

type FakeSolver struct {
	mock.Mock
}

func (m *FakeSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	args := m.Called(ctx, question)
	if msg, ok := args.Get(0).(*dns.Msg); ok {
		return msg, nil
	}
	if v, ok := args.Get(1).(error); ok {
		return nil, v
	}
	return nil, nil
}
