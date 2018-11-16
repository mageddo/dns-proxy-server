package proxy

import (
	"fmt"
	hashlru "github.com/hashicorp/golang-lru"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
	"time"
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
	solver := NewLocalDNSSolver(store.GetInstance())

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
	solver := NewLocalDNSSolver(store.GetInstance())

	// act
	_, err := solver.Solve(testCtx, *question)
	assert.NotNil(t, err, "Fail to solve")

}

//
// Testing if cache is working
// In first time must load hostname from file
// In second must load from cache
//
func TestLocalDnsSolver_SolveValidatingCache(t *testing.T) {

	defer local.ResetConf()

	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	// configuring a new host at local configuration
	expectedHostname := "github.com"
	host := local.HostnameVo{Hostname: expectedHostname, Env: "", Ttl: 50, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	// creating a request for the created host
	question := new(dns.Question)
	question.Name = expectedHostname + "."

	// stubbing cache to verify the calls
	mockCache := &MockCache{}
	mockCache.Cache, err = hashlru.New(1)
	assert.Nil(t, err, "Failed to create cache")
	mockCache.On("PutIfAbsent", expectedHostname, mock.Anything).Once()

	solver := NewLocalDNSSolver(mockCache)

	// we ask for the same host 5 times but it must load from file just once
	for i := 5; i > 0; i-- {

		// act
		res, err := solver.Solve(testCtx, *question)
		assert.Nil(t, err, "Fail to solve")

		// assert
		assert.Equal(t, 1, len(res.Answer))
		assert.Equal(t, "github.com.	0	IN	A	192.168.0.1", res.Answer[0].String())

	}

	mockCache.AssertExpectations(t)

}

func TestLocalDnsSolver_SolveCacheExpiration(t *testing.T) {

	defer local.ResetConf()

	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	// configuring a new host at local configuration
	expectedHostname := "github.com"
	host := local.HostnameVo{Hostname: expectedHostname, Env: "", Ttl: 2, Ip: [4]byte{192, 168, 0, 1}}
	conf.AddHostname( "", host)

	// creating a request for the created host
	question := new(dns.Question)
	question.Name = expectedHostname + "."

	// stubbing cache to verify the calls
	mockCache := &MockCache{}
	mockCache.Cache, err = hashlru.New(1)
	assert.Nil(t, err, "Failed to create cache")
	mockCache.On("PutIfAbsent", expectedHostname, mock.Anything).Twice()

	solver := NewLocalDNSSolver(mockCache)

	// we ask for the same host 5 times but it must load from file just once
	for i := 4; i > 0; i-- {

		time.Sleep(time.Duration(int64(1100)) * time.Millisecond)

		// act
		res, err := solver.Solve(testCtx, *question)
		assert.Nil(t, err, "Fail to solve")

		// assert
		assert.Equal(t, 1, len(res.Answer))
		assert.Equal(t, "github.com.	0	IN	A	192.168.0.1", res.Answer[0].String())

	}

	mockCache.AssertExpectations(t)

}

func TestLocalDnsSolver_SolvingByWildcard(t *testing.T) {

	// arrange
	c := lru.New(256)

	solver := NewLocalDNSSolver(c)

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

func TestLocalDnsSolver_WildcardRegisteredButNotMatched(t *testing.T) {

	// arrange
	c := lru.New(256)

	solver := NewLocalDNSSolver(c)

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
