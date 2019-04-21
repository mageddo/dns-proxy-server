package proxy

import (
	"context"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/mock"
	"testing"
)

type MockedRemoteDnsSolver struct {
	mock.Mock
	remoteDnsSolver
}

func (m MockedRemoteDnsSolver) loadConfiguration(ctx context.Context) (*localvo.Configuration, error) {
	logging.Infof("status=mocked-config")
	return local.LoadConfiguration()
}

func TestRemoteDnsSolver_SolveCacheSuccess(t *testing.T) {

	remoteSolver := new(MockedRemoteDnsSolver)
	remoteSolver.confloader = func(ctx context.Context) (*localvo.Configuration, error) {
		remoteSolver.MethodCalled("confloader", ctx)
		return local.LoadConfiguration()
	}

	remoteSolver.On("confloader", testCtx).Once()

	question := new(dns.Question)
	remoteSolver.Solve(testCtx, *question)
	remoteSolver.Solve(testCtx, *question)

	remoteSolver.AssertExpectations(t)

}

