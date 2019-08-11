package dockernetwork

import (
	"context"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/filters"
	"github.com/docker/engine-api/types/network"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/pkg/errors"
	"strings"
)

const DpsNetwork = "dps"
func CreateOrUpdateDpsNetwork(ctx context.Context, cli *client.Client) (types.NetworkCreateResponse, error) {
	res, err := cli.NetworkCreate(ctx, DpsNetwork, types.NetworkCreate{
		CheckDuplicate: true,
		Driver:         "bridge",
		EnableIPv6:     false,
		IPAM:           &network.IPAM{
			Options: nil,
			Config:  []network.IPAMConfig{{
				Subnet:  "172.157.0.0/16",
				IPRange: "172.157.5.3/24",
				Gateway: "172.157.5.254",
			}},
		},
		Internal:       false,
		Attachable:     true,
		Options:        nil,
		Labels: map[string]string{
			"description":"this is a Dns Proxy Server Network",
			"version": flags.GetRawCurrentVersion(),
		},
	})
	if err == nil {
		return res, nil
	} else if strings.Contains(err.Error(), fmt.Sprintf("network with name %s already exists", DpsNetwork)) {
		//return findNetwork(ctx, cli, DpsNetwork), nil
		return res, nil
	}
	return res, err
}

//func findNetwork(ctx context.Context, client *client.Client, name string) types.NetworkCreateResponse {
//	client.net
//}

func NetworkConnect(ctx context.Context, cli *client.Client, networkId string, containerId string, networkIpAddress string) error {
	//if err := cli.NetworkDisconnect(ctx, networkId, containerId, true);
	//	err != nil &&
	//	!strings.Contains(err.Error(), fmt.Sprintf("is not connected to network %s", DpsNetwork)) {
	//	panic(fmt.Sprintf("could not disconnect dps container from dps network: %+v", err))
	//}
	err := cli.NetworkConnect(ctx, networkId, containerId, &network.EndpointSettings{
		NetworkID: networkId,
		IPAddress: networkIpAddress,
		IPAMConfig: &network.EndpointIPAMConfig{
			IPv4Address: networkIpAddress,
		},
	})
	if err != nil && strings.Contains(err.Error(), "already exists in network")  {
		return nil
	}
	return err
}

func FindDpsContainer(ctx context.Context, cli *client.Client) (*types.Container, error) {
	if args, err := filters.ParseFlag("label=dps.container=true", filters.NewArgs()); err != nil {
		return nil, errors.WithMessage(err, "can't parse flags")
	} else {
		if containers, err := cli.ContainerList(ctx, types.ContainerListOptions{
			Filter: args,
		}); err != nil {
			return nil, errors.WithMessage(err, "can't list containers")
		} else {
			if len(containers) == 1 {
				return &containers[0], nil
			} else {
				return nil, errors.New(fmt.Sprintf("containers result must be exactly one but found: %d", len(containers)))
			}
		}
	}
}

