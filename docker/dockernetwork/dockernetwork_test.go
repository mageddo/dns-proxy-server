package dockernetwork

import (
	"fmt"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/network"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"strings"
	"testing"
)

func TestGetGatewayIp_MustFindNetworkFromDps(t *testing.T) {
	// arrange
	ctx := reference.Context()
	mockApiClient := &MockApiClient{}
	SetCli(mockApiClient)

	mockApiClient.On("NetworkList", ctx, mock.MatchedBy(func(it interface{}) bool {
		return strings.Contains(fmt.Sprintf("%+v", it), "dps")
	})).
		Return([]types.NetworkResource{{
			Driver: "dps",
			IPAM: network.IPAM{
				Config: []network.IPAMConfig{{
					Gateway: "192.168.0.1",
				}},
			},
		}}, nil)

	// act
	ip, err := GetGatewayIp(ctx)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, ip, "192.168.0.1")
}

func TestGetGatewayIp_MustFindNetworkFromBridge(t *testing.T) {
	// arrange
	ctx := reference.Context()
	mockApiClient := &MockApiClient{}
	SetCli(mockApiClient)

	mockApiClient.On("NetworkList", ctx, mock.MatchedBy(func(it interface{}) bool {
		return strings.Contains(fmt.Sprintf("%+v", it), "dps")
	})).
		Return([]types.NetworkResource{}, nil)

	mockApiClient.On("NetworkList", ctx, mock.MatchedBy(func(it interface{}) bool {
		return strings.Contains(fmt.Sprintf("%+v", it), "bridge")
	})).
		Return([]types.NetworkResource{{
			Driver: "bridge",
			IPAM: network.IPAM{
				Config: []network.IPAMConfig{{
					Gateway: "192.168.0.1",
				}},
			},
		}}, nil)

	// act
	ip, err := GetGatewayIp(ctx)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, ip, "192.168.0.1")
}
