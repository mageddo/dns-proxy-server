package proxy

import (
	"context"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
)

var testCtx = context.Background()

func TestMustCacheWhenResultIsSuccessFull(t *testing.T){

	// arrange
	c := &FakeSolver{}
	solver := NewCacheDnsSolver(c)
	q := dns.Question{Name: "acme.com."}
	c.On("Solve", ctx, q).Return(&dns.Msg{}, nil)

	for i := 0; i < 2; i++ {
		// act
		msg, err := solver.Solve(testCtx, q)

		// assert
		assert.Nil(t, err)
		assert.NotNil(t, msg)
	}

	c.AssertNumberOfCalls(t, "Solve", 1)
}

type FakeSolver struct {
	mock.Mock
}

func (m *FakeSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	args := m.Called(ctx, question)
	if v, ok := args.Get(1).(error); ok {
		return args.Get(0).(*dns.Msg), v
	}
	return args.Get(0).(*dns.Msg), nil
}
