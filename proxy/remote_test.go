package proxy

import (
	"testing"
	"github.com/stretchr/testify/mock"
	"github.com/miekg/dns"
	"github.com/mageddo/go-logging"
	"github.com/mageddo/dns-proxy-server/events/local"
	"context"
)

type MockedRemoteDnsSolver struct {
	mock.Mock
	remoteDnsSolver
}

func (m MockedRemoteDnsSolver) loadConfiguration(ctx context.Context) (*local.LocalConfiguration, error) {
	logging.Infof("status=mocked-config")
	return local.LoadConfiguration()
}

func TestRemoteDnsSolver_SolveCacheSuccess(t *testing.T) {

	remoteSolver := new(MockedRemoteDnsSolver)
	remoteSolver.confloader = func(ctx context.Context) (*local.LocalConfiguration, error) {
		remoteSolver.MethodCalled("confloader", ctx)
		return local.LoadConfiguration()
	}

	remoteSolver.On("confloader", testCtx).Once()

	question := new(dns.Question)
	remoteSolver.Solve(testCtx, *question)
	remoteSolver.Solve(testCtx, *question)

	remoteSolver.AssertExpectations(t)

}

