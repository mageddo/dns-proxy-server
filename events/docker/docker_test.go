package docker

import (
	"fmt"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/api/types/network"
	"github.com/mageddo/dns-proxy-server/docker/dockernetwork"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/dns-proxy-server/utils/env"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"os"
	"strings"
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
	os.Setenv(env.MG_DOMAIN, "other.example.com")

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
	inspect.Name = "/nginx-container_1"

	// assert
	hosts := getHostnames(inspect)

	// act
	assert.Equal(t, []string {"mageddo.com", "server2.mageddo.com", "server3.mageddo.com", "nginx-container_1.other.example.com", "nginx-service.other.example.com"}, hosts)

}

func TestMustSolveIPFromDefaultConfiguredNetwork(t *testing.T){
	// arrange
	mockApiClient := &dockernetwork.MockApiClient{}
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
	inspect.NetworkSettings.Networks["network-1"] = mockApiClient.CreateMockNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks["network-2"] = mockApiClient.CreateMockNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = mockApiClient.CreateMockNetwork("192.168.0.3", "125")

	// act
	putHostnames(reference.Context(), []string{"acme.com"}, inspect)

	// assert
	assert.Equal(t, "192.168.0.2", c.Get("acme.com"))
}

func TestMustSolveIPFromFirstContainerWhenDefaultNetworkIsNotSet(t *testing.T){
	// arrange
	ctx := reference.Context()
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Labels: map[string]string{},
		},
	}
	mockApiClient := &dockernetwork.MockApiClient{}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.NetworkSettings = new(types.NetworkSettings)
	inspect.NetworkSettings.Networks = make(map[string]*network.EndpointSettings)
	inspect.NetworkSettings.Networks["network-1"] = mockApiClient.CreateMockNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks["network-2"] = mockApiClient.CreateMockNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = mockApiClient.CreateMockNetwork("192.168.0.3", "125")

	dockernetwork.SetCli(mockApiClient)

	mockApiClient.On("NetworkList", ctx, types.NetworkListOptions{
		Filters: dockernetwork.MustParseFlags(fmt.Sprintf("id=^%s", "124")),
	}).
	Return([]types.NetworkResource{{
		Driver: "bridge",
	}}, nil)

	mockApiClient.On("NetworkList", ctx, mock.MatchedBy(func(it interface{}) bool {
		return !strings.Contains(fmt.Sprintf("%+v", it), "124")
	})).
	Return([]types.NetworkResource{{
		Driver: "overlay",
	}}, nil)

	// act
	putHostnames(ctx, []string{"acme.com"}, inspect)
	foundHostname := c.Get("acme.com")

	// assert
	assert.Equal(t, "192.168.0.2", foundHostname)
}

func TestMustSolveIPFromDpsNetworkWhenSet(t *testing.T){
	// arrange
	mockApiClient := &dockernetwork.MockApiClient{}
	inspect := types.ContainerJSON{
		Config: &container.Config{
			Labels: map[string]string{
				"com.docker.compose.service": "nginx-service",
			},
		},
	}
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.Name = "/nginx-container"
	inspect.NetworkSettings = new(types.NetworkSettings)
	inspect.NetworkSettings.Networks = make(map[string]*network.EndpointSettings)
	inspect.NetworkSettings.Networks["network-1"] = mockApiClient.CreateMockNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks[dockernetwork.DpsNetwork] = mockApiClient.CreateMockNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = mockApiClient.CreateMockNetwork("192.168.0.3", "125")

	// act
	putHostnames(reference.Context(), []string{"acme.com"}, inspect)
	foundHostname := c.Get("acme.com")

	// assert
	assert.Equal(t, foundHostname, "192.168.0.2")
}

