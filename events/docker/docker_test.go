package docker

import (
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/container"
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
