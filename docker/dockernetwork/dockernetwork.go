package dockernetwork

import (
	"context"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/filters"
	"github.com/docker/engine-api/types/network"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/go-logging"
	"github.com/pkg/errors"
	"strings"
)

const DpsNetwork = "dps"
var cli *client.Client = nil

func GetCli() *client.Client {
	return cli
}

func SetCli(cli_ *client.Client){
	cli = cli_
}

func IsDockerConnected() bool {
	return cli != nil
}

func CreateOrUpdateDpsNetwork(ctx context.Context) (types.NetworkCreateResponse, error) {
	res, err := cli.NetworkCreate(ctx, DpsNetwork, types.NetworkCreate{
		CheckDuplicate: true,
		Driver:         "bridge",
		EnableIPv6:     false,
		IPAM:           &network.IPAM{
			Options: nil,
			Config:  []network.IPAMConfig{{
				Subnet:  "172.157.0.0/16",
				IPRange: "172.157.5.3/24",
				Gateway: "172.157.5.1",
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
	if err == nil || alreadyCreated(err) {
		return res, nil
	}
	return res, err
}

func FindNetworkGatewayIp(ctx context.Context, name string) (string, error) {
	if networkResource, err := FindNetwork(ctx, name); err != nil {
		return "", err
	} else  {
		return networkResource.IPAM.Config[0].Gateway, nil
	}
}

func FindDpsNetworkGatewayIp(ctx context.Context) (string, error) {
	return FindNetworkGatewayIp(ctx, DpsNetwork)
}

func FindDpsNetwork(ctx context.Context) (*types.NetworkResource, error) {
	return FindNetwork(ctx, DpsNetwork)
}

func FindNetwork(ctx context.Context, name string) (*types.NetworkResource, error) {
	args, err := filters.ParseFlag(fmt.Sprintf("name=^%s$", name), filters.NewArgs())
	if err != nil {
		panic(errors.WithMessage(err, "can't parse args"))
	}
	networks, err := cli.NetworkList(ctx, types.NetworkListOptions{Filters: args})
	if err != nil {
		return nil, errors.WithMessage(err, "can't list networks")
	} else if len(networks) == 1 {
		return &networks[0], nil
	}
	return nil, errors.New("didn't found the specified network: " + name)
}

func MustNetworkDisconnectForIp(ctx context.Context, networkId string, containerIP string) {
	if foundNetwork, err := FindNetwork(ctx, networkId); err != nil {
		panic(errors.WithMessage(err, fmt.Sprintf("can't find network=%s", networkId)))
	} else {
		for containerId, container := range foundNetwork.Containers {
			if strings.Contains(container.IPv4Address, containerIP) {
				logging.Infof("status=detaching-another-dps, ip=%s, old-container=%s", containerIP, container.Name)
				MustNetworkDisconnect(ctx, networkId, containerId)
			}
		}
	}
}

func MustNetworkDisconnect(ctx context.Context, networkId, containerId string){
	if err := cli.NetworkDisconnect(ctx, networkId, containerId, true);
		err != nil &&
		!strings.Contains(err.Error(), fmt.Sprintf("is not connected to network %s", DpsNetwork)) {
		panic(fmt.Sprintf("could not disconnect dps container from dps network: %+v", err))
	}
}

func MustNetworkConnect(ctx context.Context, networkId string, containerId string, networkIpAddress string) {
	if err := NetworkConnect(ctx, networkId, containerId, networkIpAddress); err != nil {
		panic(errors.WithMessage(err, fmt.Sprintf(
			"can't connect container %s to network %s, ip=%s", containerId, networkId, networkIpAddress,
		)))
	} else {
		logging.Infof("status=network-connected, network=%s, container=%s", ctx, networkId, containerId)
	}
}

func NetworkConnect(ctx context.Context, networkId string, containerId string, networkIpAddress string) error {
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

func FindDpsContainer(ctx context.Context) (*types.Container, error) {
	logging.Debugf("cli=%+v", cli)
	if containers, err := cli.ContainerList(ctx, types.ContainerListOptions {
		Filter: mustParseDpsContainerFlags(),
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

func mustParseDpsContainerFlags() filters.Args {
	if args, err := filters.ParseFlag("label=dps.container=true", filters.NewArgs()); err != nil {
		panic(errors.WithMessage(err, "can't parse flags"))
	} else {
		return args
	}
}

func FindDpsContainerIP(ctx context.Context) (string, error) {
	container, err := FindDpsContainer(ctx)
	if err != nil {
		return "", err
	}
	if containerJSON, err := cli.ContainerInspect(ctx, container.ID); err == nil {
		return FindBestIP(containerJSON), nil
	} else {
		return "", errors.WithMessage(err, fmt.Sprintf("can't inspect container: %+v", container.Names))
	}
}

func FindBestIP(container types.ContainerJSON) string {
	return FindBestIPForNetworks(container, DpsNetwork, "bridge")
}

func FindBestIPForNetworks(container types.ContainerJSON, networks ... string) string {
	// first, find on preferred networks
	for _, network := range networks {
		if ip := GetIPFromNetworksMap(container.NetworkSettings.Networks, network); ip != "" {
			return ip
		}
	}
	for network, _ := range container.NetworkSettings.Networks {
		if ip := GetIPFromNetworksMap(container.NetworkSettings.Networks, network); ip != "" {
			return ip
		}
	}
	return container.NetworkSettings.IPAddress
}

func GetIPFromNetworksMap(networks map[string]*network.EndpointSettings, key string) string {
	theNetwork := networks[key]
	if theNetwork == nil {
		return ""
	}
	return theNetwork.IPAddress
}

func alreadyCreated(err error) bool {
	return strings.Contains(err.Error(), fmt.Sprintf("network with name %s already exists", DpsNetwork))
}
