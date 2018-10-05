package docker

import (
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/container"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGetHostnames(t *testing.T){

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
