package proxy

import (
	glru "github.com/hashicorp/golang-lru"
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
)

var ctx = reference.Context()

func TestDockerSolve_HostFound(t *testing.T){
	c := newCacheMock()
	solver := NewDockerSolver(c)
	c.Put("host1.com", "127.0.0.1");

	q := dns.Question{Name: "host1.com."}

	msg, err := solver.Solve(ctx, q)
	assert.Nil(t, err)
	assert.NotNil(t, msg)

}

func TestDockerSolve_HostNotFound(t *testing.T){
	c := newCacheMock()

	solver := NewDockerSolver(c)

	q := dns.Question{Name: "host1.com."}
	msg, err := solver.Solve(ctx, q)
	assert.Nil(t, msg)
	assert.NotNil(t, err)

}

func TestDockerSolve_WildcardDomain(t *testing.T){
	c := newCacheMock()
	c.Put(".host1.com", "127.0.0.1");

	solver := NewDockerSolver(c)

	q := dns.Question{Name: "sub1.host1.com."}
	msg, err := solver.Solve(ctx, q)
	assert.Nil(t, err)
	assert.NotNil(t, msg)

}

type CacheMock struct {
	mock.Mock
	lru.LRUCache
}

func newCacheMock() cache.Cache {
	var err error;
	m := CacheMock{}
	m.Cache, err = glru.New(10)
	if err != nil {
		panic(err)
	}
	return &m
}
