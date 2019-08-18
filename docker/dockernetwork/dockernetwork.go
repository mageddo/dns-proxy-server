package dockernetwork

import (
	"context"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/filters"
	"github.com/docker/engine-api/types/network"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/go-logging"
	"github.com/pkg/errors"
	"sort"
	"strings"
)

const DpsNetwork = "dps"
var cli client.APIClient = nil

func GetCli() client.APIClient {
	return cli
}

func SetCli(cli_ client.APIClient){
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
	if networkResource, err := FindNetworkByName(ctx, name); err != nil {
		return "", err
	} else  {
		return networkResource.IPAM.Config[0].Gateway, nil
	}
}

func FindDpsNetworkGatewayIp(ctx context.Context) (string, error) {
	return FindNetworkGatewayIp(ctx, DpsNetwork)
}

func FindDpsNetwork(ctx context.Context) (*types.NetworkResource, error) {
	return FindNetworkByName(ctx, DpsNetwork)
}

func FindNetworkByName(ctx context.Context, name string) (*types.NetworkResource, error) {
	return FindNetwork(ctx, fmt.Sprintf("name=^%s$", name))
}

func FindNetworkByID(ctx context.Context, id string) (*types.NetworkResource, error) {
	return FindNetwork(ctx, fmt.Sprintf("id=^%s", id))
}

func FindNetwork(ctx context.Context, args ... string) (*types.NetworkResource, error) {
	networks, err := cli.NetworkList(ctx, types.NetworkListOptions{Filters: MustParseFlags(args...)})
	if err != nil {
		return nil, errors.WithMessage(err, "can't list networks")
	} else if len(networks) == 1 {
		return &networks[0], nil
	}
	return nil, errors.New(fmt.Sprintf("didn't found the specified network with args: %+v", args))
}

func MustNetworkDisconnectForIp(ctx context.Context, networkName string, containerIP string) {
	if foundNetwork, err := FindNetworkByName(ctx, networkName); err != nil {
		panic(errors.WithMessage(err, fmt.Sprintf("can't find network=%s", networkName)))
	} else {
		for containerId, container := range foundNetwork.Containers {
			if strings.Contains(container.IPv4Address, containerIP) {
				logging.Infof("status=detaching-another-dps, ip=%s, old-container=%s", containerIP, container.Name)
				MustNetworkDisconnect(ctx, networkName, containerId)
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
	if !conf.DpsNetworkAutoConnect() {
		return
	}
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
		Filter: MustParseFlags("status=running", "label=dps.container=true"),
	}); err != nil {
		return nil, errors.WithMessage(err, "can't list containers")
	} else {
		if len(containers) == 1 {
			return &containers[0], nil
		} else if len(containers) > 1 {
			logging.Warningf(
				"status=multiple-dps-containers-found, action=using-the-first, containers=%+v", toContainerNames(containers),
			)
			return &containers[0], nil
		} else {
			return nil, errors.New(fmt.Sprintf("containers result must be exactly one but found: %d", len(containers)))
		}
	}
}

func toContainerNames(containers []types.Container) []string {
	containersNames := make([]string, len(containers))
	for i, container := range containers {
		containersNames[i] = container.Names[0]
	}
	return containersNames
}

func MustParseFlags(flags ... string) filters.Args {
	args := filters.NewArgs()
	for _, filter := range flags {
		var err error
		if args, err = filters.ParseFlag(filter, args); err != nil {
			panic(errors.WithMessage(err, "can't parse flags"))
		}
	}
	return args
}

func FindDpsContainerIP(ctx context.Context) (string, error) {
	container, err := FindDpsContainer(ctx)
	if err != nil {
		return "", err
	}
	if containerJSON, err := cli.ContainerInspect(ctx, container.ID); err == nil {
		return FindBestIP(ctx, containerJSON), nil
	} else {
		return "", errors.WithMessage(err, fmt.Sprintf("can't inspect container: %+v", container.Names))
	}
}

func FindBestIP(ctx context.Context, container types.ContainerJSON) string {
	return FindBestIpForNetworks(ctx, container, DpsNetwork, "bridge")
}

func FindBestIpForNetworks(ctx context.Context, container types.ContainerJSON, preferredNetworks ... string) string {
	// first, find on preferred networks
	for _, networkInspect := range preferredNetworks {
		if ip := GetIPFromNetworksMap(container.NetworkSettings.Networks, networkInspect); ip != "" {
			return ip
		}
	}

	completeNetworks := constructCompleteNetwork(ctx, MapValues(container.NetworkSettings.Networks))
	sort.Sort(CompleteNetworkByDriver(completeNetworks))
	for _, completeNetwork := range completeNetworks {
		if completeNetwork.IpAddress != "" {
			return completeNetwork.IpAddress
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

func DisconnectNetworkContainers(ctx context.Context, networkId string) []error {
	if networkResource, err := FindNetworkByID(ctx, networkId); err != nil {
		return []error{errors.WithMessage(err, fmt.Sprintf("cant inspect network: %s", networkId))}
	} else {
		var errs []error
		for cid := range networkResource.Containers {
			if err := cli.NetworkDisconnect(ctx, networkId, cid, true); err != nil {
				errs = append(errs, errors.WithMessage(err, fmt.Sprintf("cant disconnect container %s", cid)))
			} else {
				errs = append(errs, errors.New(fmt.Sprintf("success for %s", cid)))
			}
		}
		return errs
	}
}

func alreadyCreated(err error) bool {
	return strings.Contains(err.Error(), fmt.Sprintf("network with name %s already exists", DpsNetwork))
}

func MapValues(endpointsMap map[string]*network.EndpointSettings) []*network.EndpointSettings {
	values := make([]*network.EndpointSettings, 0, len(endpointsMap))
	for _, v := range endpointsMap {
		values = append(values, v)
	}
	return values
}

func constructCompleteNetwork(ctx context.Context, endpoints []*network.EndpointSettings) []*CompleteNetwork {
	completeNetworks := make([]*CompleteNetwork, len(endpoints))
	for i, endpoint := range endpoints {
		if networkRes, err := FindNetworkByID(ctx, endpoint.NetworkID); err != nil {
			panic(errors.WithMessage(err, fmt.Sprintf("can't inspect network: %s", endpoint.NetworkID)))
		} else {
			completeNetworks[i] = &CompleteNetwork{
				IpAddress: endpoint.IPAddress,
				NetworkId: endpoint.NetworkID,
				Driver:    networkRes.Driver,
			}
		}
	}
	return completeNetworks
}

type CompleteNetwork struct {
	IpAddress string
	NetworkId string
	Driver string
}

type CompleteNetworkByDriver []*CompleteNetwork
func (a CompleteNetworkByDriver) Len() int { return len(a) }
func (a CompleteNetworkByDriver) Swap(i, j int) { a[i], a[j] = a[j], a[i] }
func (a CompleteNetworkByDriver) Less(i, j int) bool {
	return getDriverOrder(a[i].Driver) < getDriverOrder(a[j].Driver)
}
func getDriverOrder(driver string) int {
	switch driver {
	case "bridge":
		return 0
	default:
		return 1
	}
}
