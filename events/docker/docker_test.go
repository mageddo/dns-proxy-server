package docker

import (
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/container"
	"github.com/docker/engine-api/types/network"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/dns-proxy-server/utils/env"
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

func TestMustGetHostnamesBasedOnMachineHostnameAndEnvironmentVariable(t *testing.T){

	// arrange
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Hostname:"mageddo", Domainname:"com",
			Env: []string{"HOSTNAMES=server2.mageddo.com,server3.mageddo.com"},
		},
	}

	// assert
	hosts := getHostnames(inspect)

	// act
	assert.Equal(t, []string {"mageddo.com", "server2.mageddo.com", "server3.mageddo.com"}, hosts)

}

func TestContainerNamesRegistryMustBeDisabledByDefault(t *testing.T){

	os.Setenv(env.MG_REGISTER_CONTAINER_NAMES, "0")

	// arrange
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Hostname:"mageddo", Domainname:"com",
			Env: []string{"HOSTNAMES=server2.mageddo.com,server3.mageddo.com"},
			Labels: map[string]string{
				"com.docker.compose.service": "nginx-service",
			},
		},
	}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.Name = "/nginx-container"

	// assert
	hosts := getHostnames(inspect)

	// act
	assert.Equal(t, []string {"mageddo.com", "server2.mageddo.com", "server3.mageddo.com"}, hosts)

}

func TestMustGetHostnamesBasedOnMachineHostnameAndEnvironmentVariableAndContainerNameAndContainerServiceName(t *testing.T){

	os.Setenv(env.MG_REGISTER_CONTAINER_NAMES, "1")

	// arrange
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Hostname:"mageddo", Domainname:"com",
			Env: []string{"HOSTNAMES=server2.mageddo.com,server3.mageddo.com"},
			Labels: map[string]string{
				"com.docker.compose.service": "nginx-service",
			},
		},
	}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.Name = "/nginx-container"

	// assert
	hosts := getHostnames(inspect)

	// act
	assert.Equal(t, []string {"mageddo.com", "server2.mageddo.com", "server3.mageddo.com", "nginx-container.docker", "nginx-service.docker"}, hosts)

}

func TestMustSolveIPFromDefaultConfiguredNetwork(t *testing.T){
	// arrange
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Hostname:"mageddo", Domainname:"com",
			Env: []string{"HOSTNAMES=server2.mageddo.com,server3.mageddo.com"},
			Labels: map[string]string{
				"com.docker.compose.service": "nginx-service",
			},
		},
	}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.Name = "/nginx-container"
	inspect.Config.Labels[defaultNetworkLabel] = "network-2"
	inspect.NetworkSettings = new(types.NetworkSettings)
	inspect.NetworkSettings.Networks = make(map[string]*network.EndpointSettings)
	inspect.NetworkSettings.Networks["network-1"] = createNetwork("192.168.0.1")
	inspect.NetworkSettings.Networks["network-2"] = createNetwork("192.168.0.2")
	inspect.NetworkSettings.Networks["network-3"] = createNetwork("192.168.0.3")

	// act
	putHostnames(reference.Context(), []string{"acme.com"}, inspect)

	// assert
	assert.Equal(t, "192.168.0.2", c.Get("acme.com"))
}


func TestMustSolveIPFromFirstContainerWhenDefaultNetworkIsNotSet(t *testing.T){
	// arrange
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Hostname:"mageddo", Domainname:"com",
			Env: []string{"HOSTNAMES=server2.mageddo.com,server3.mageddo.com"},
			Labels: map[string]string{
				"com.docker.compose.service": "nginx-service",
			},
		},
	}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.Name = "/nginx-container"
	inspect.NetworkSettings = new(types.NetworkSettings)
	inspect.NetworkSettings.Networks = make(map[string]*network.EndpointSettings)
	inspect.NetworkSettings.Networks["network-1"] = createNetwork("192.168.0.1")
	inspect.NetworkSettings.Networks["network-2"] = createNetwork("192.168.0.2")
	inspect.NetworkSettings.Networks["network-3"] = createNetwork("192.168.0.3")

	// act
	putHostnames(reference.Context(), []string{"acme.com"}, inspect)

	// assert
	assert.Contains(t, c.Get("acme.com"), "192.168.0")
}

func createNetwork(ip string) *network.EndpointSettings {
	m := new(network.EndpointSettings)
	m.IPAddress = ip
	return m
}
