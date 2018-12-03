package proxy

import (
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
)

func TestLocalDnsSolver_Solve(t *testing.T) {

	defer local.ResetConf()

	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	expectedHostname := "github.com"
	host := local.HostnameVo{Hostname: expectedHostname, Env: "", Ttl: 50, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	question := new(dns.Question)
	question.Name = expectedHostname + "."
	solver := NewLocalDNSSolver()

	// act
	res, err := solver.Solve(testCtx, *question)
	assert.Nil(t, err, "Fail to solve")

	// assert
	assert.Equal(t, 1, len(res.Answer))
	assert.Equal(t, "github.com.	0	IN	A	192.168.0.1", res.Answer[0].String())

}

func TestLocalDnsSolver_SolveNotFoundHost(t *testing.T) {

	defer local.ResetConf()

	expectedHostname := "github.com"
	question := new(dns.Question)
	question.Name = expectedHostname + "."
	solver := NewLocalDNSSolver()

	// act
	_, err := solver.Solve(testCtx, *question)
	assert.NotNil(t, err, "Fail to solve")

}

func TestLocalDnsSolver_SolvingByWildcardFirstLevel(t *testing.T) {

	// arrange
	solver := NewLocalDNSSolver()

	defer local.ResetConf()
	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	host := local.HostnameVo{Hostname: ".github.com", Env: "", Ttl: 2, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	question := new(dns.Question)
	question.Name = "server1.github.com."

	// act
	res, err := solver.Solve(testCtx, *question)

	// assert
	assert.Nil(t, err, "Fail to solve")
	assert.Equal(t, 1, len(res.Answer))
	assert.Equal(t, "server1.github.com.	0	IN	A	192.168.0.1", res.Answer[0].String())

}

func TestLocalDnsSolver_SolvingByWildcardSecondLevel(t *testing.T) {

	// arrange
	solver := NewLocalDNSSolver()

	defer local.ResetConf()
	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	host := local.HostnameVo{Hostname: ".github.com", Env: "", Ttl: 2, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	question := new(dns.Question)
	question.Name = "site.server1.github.com."

	// act
	res, err := solver.Solve(testCtx, *question)

	// assert
	assert.Nil(t, err, "Fail to solve")
	assert.Equal(t, 1, len(res.Answer))
	assert.Equal(t, "site.server1.github.com.	0	IN	A	192.168.0.1", res.Answer[0].String())

}

func TestLocalDnsSolver_WildcardRegisteredButNotMatched(t *testing.T) {

	// arrange
	solver := NewLocalDNSSolver()

	defer local.ResetConf()
	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	host := local.HostnameVo{Hostname: ".github.com", Env: "", Ttl: 2, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	question := new(dns.Question)
	question.Name = "server1.mageddo.com."

	// act
	res, err := solver.Solve(testCtx, *question)

	// assert
	assert.NotNil(t, err, "Fail to solve")
	assert.Nil(t, res)

}

type MockCache struct {
	mock.Mock
	lru.LRUCache
}

//
// spy put method
//
func (m *MockCache) PutIfAbsent(key, value interface{}) interface{} {
	fmt.Println("mocked!!!!!")
	m.Called(key, value)
	return m.LRUCache.PutIfAbsent(key, value)
}
