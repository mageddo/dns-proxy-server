package dockernetwork

import (
	"context"
	"github.com/docker/docker/client"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/network"
	"github.com/stretchr/testify/mock"
)

type MockApiClient struct {
	mock.Mock
	client.Client
}

func (m *MockApiClient) NetworkList(ctx context.Context, options types.NetworkListOptions) ([]types.NetworkResource, error) {
	args := m.Called(ctx, options)
	return args.Get(0).([]types.NetworkResource), args.Error(1)
}

func (*MockApiClient) CreateMockNetwork(ip string, id string) *network.EndpointSettings {
	m := new(network.EndpointSettings)
	m.IPAddress = ip
	m.NetworkID = id
	return m
}
