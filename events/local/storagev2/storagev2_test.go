package storagev2

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestValueOf(t *testing.T) {
	// arrange
	var config = localvo.Configuration{
		Version:                3,
		RemoteDnsServers:       nil,
		Envs:                   nil,
		ActiveEnv:              "",
		WebServerPort:          80,
		DnsServerPort:          53,
		DefaultDns:             nil,
		LogLevel:               "DEBUG",
		LogFile:                "console",
		RegisterContainerNames: nil,
		HostMachineHostname:    "",
		Domain:                 "docker",
		DpsNetwork:             nil,
		DpsNetworkAutoConnect:  nil,
	}

	// act
	v2Config := ValueOf(&config)

	// assert
	assert.Equal(t, int64(2), v2Config.Version)
	assert.Equal(t, make([]string, 0),  v2Config.RemoteDnsServers)
	assert.Equal(t, make([]EnvV2, 0),  v2Config.Envs)
	assert.Equal(t, "", v2Config.ActiveEnv)
	assert.Equal(t, 80, v2Config.WebServerPort)
	assert.Equal(t, 53, v2Config.DnsServerPort)
	assert.Nil(t,  nil, v2Config.DefaultDns)
	assert.Equal(t, "DEBUG", v2Config.LogLevel)
	assert.Equal(t, "console", v2Config.LogFile)
	assert.Nil(t, v2Config.RegisterContainerNames)
	assert.Equal(t, "",  v2Config.HostMachineHostname)
	assert.Equal(t, "docker", v2Config.Domain)
	assert.Nil(t, v2Config.DpsNetwork)
	assert.Nil(t, v2Config.DpsNetworkAutoConnect)

}
