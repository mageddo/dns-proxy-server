package local

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"testing"
	"github.com/stretchr/testify/assert"
	"github.com/mageddo/dns-proxy-server/cache/store"
)

func TestSaveConfiguration_ClearCacheAfterChangeConfiguration(t *testing.T) {

	// arrange

	ResetConf()

	expectedHostname := "github.io"

	conf, err := LoadConfiguration()
	assert.Nil(t, err, "could not load conf")

	cache := store.GetInstance()
	env, _ := conf.GetActiveEnv()

	assert.False(t, cache.ContainsKey(expectedHostname))
	foundHostname, _ := env.GetHostname(expectedHostname)
	assert.Nil(t, foundHostname)

	// setting the host
	cache.Put(expectedHostname, foundHostname)
	assert.True(t, cache.ContainsKey(expectedHostname))
	assert.Nil(t, cache.Get(expectedHostname))

	// changing value for the hostname at configuration database
	hostname := localvo.Hostname{Ip: [4]byte{192,168,0,2}, Ttl:30, Hostname: expectedHostname, Type:"A"}
	assert.Nil(t, AddHostname( "", hostname))

	// cache must be clear after add a hostname in conf
	assert.False(t, cache.ContainsKey(expectedHostname))

	conf, err = LoadConfiguration()
	env, _  = conf.GetActiveEnv()

	foundHostname, _ = env.GetHostname(expectedHostname)
	assert.Equal(t, [4]byte{192,168,0,2}, foundHostname.Ip)

}

func TestShouldSaveARecord(t *testing.T) {

	// arrange
	ResetConf()

	expectedHostname := "github.io"

	conf, err := LoadConfiguration()
	assert.Nil(t, err, "could not load conf")

	// act
	assert.Nil(t, conf.AddHostname( "", localvo.Hostname{Ip: [4]byte{192,168,0,2}, Ttl:30, Hostname: expectedHostname, Type:localvo.A}))

	// assert

	env, _ := conf.GetActiveEnv()
	hostnameVo, _ := env.GetHostname("github.io")
	assert.Equal(t, expectedHostname, hostnameVo.Hostname)
	assert.Equal(t, localvo.A, hostnameVo.Type)

}


func TestShouldSaveCnameRecord(t *testing.T) {

	// arrange

	ResetConf()

	expectedHostname := "github.io"

	conf, err := LoadConfiguration()
	assert.Nil(t, err, "could not load conf")

	// act
	assert.Nil(t, conf.AddHostname( "", localvo.Hostname{Ip: [4]byte{192,168,0,2}, Ttl:30, Hostname: expectedHostname, Type:localvo.CNAME}))

	// assert

	env, _ := conf.GetActiveEnv()
	hostnameVo, _ := env.GetHostname("github.io")
	assert.Equal(t, expectedHostname, hostnameVo.Hostname)
	assert.Equal(t, localvo.CNAME, hostnameVo.Type)

}
