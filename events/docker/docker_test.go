package docker

import (
	"context"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/container"
	"github.com/docker/engine-api/types/network"
	"github.com/mageddo/dns-proxy-server/docker/dockernetwork"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/dns-proxy-server/utils/env"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"os"
	"strings"
	"testing"
)

type MockApiClient struct {
	mock.Mock
	client.Client
}

func (m *MockApiClient) NetworkList(ctx context.Context, options types.NetworkListOptions) ([]types.NetworkResource, error) {
	args := m.Called(ctx, options)
	return args.Get(0).([]types.NetworkResource), args.Error(1)
}

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
	inspect.NetworkSettings.Networks["network-1"] = createNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks["network-2"] = createNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = createNetwork("192.168.0.3", "125")

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
	inspect.ContainerJSONBase = new(types.ContainerJSONBase)
	inspect.NetworkSettings = new(types.NetworkSettings)
	inspect.NetworkSettings.Networks = make(map[string]*network.EndpointSettings)
	inspect.NetworkSettings.Networks["network-1"] = createNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks["network-2"] = createNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = createNetwork("192.168.0.3", "125")

	mockApiClient := &MockApiClient{}
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
	inspect.NetworkSettings.Networks[dockernetwork.DpsNetwork] = createNetwork("192.168.0.1", "123")
	inspect.NetworkSettings.Networks["network-2"] = createNetwork("192.168.0.2", "124")
	inspect.NetworkSettings.Networks["network-3"] = createNetwork("192.168.0.3", "125")

	// act
	putHostnames(reference.Context(), []string{"acme.com"}, inspect)
	foundHostname := c.Get("acme.com")

	// assert
	assert.Equal(t, foundHostname, "192.168.0.1")
}

func createNetwork(ip string, id string) *network.EndpointSettings {
	m := new(network.EndpointSettings)
	m.IPAddress = ip
	m.NetworkID = id
	return m
}
