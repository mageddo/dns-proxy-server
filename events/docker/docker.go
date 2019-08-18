package docker

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/events"
	"github.com/docker/engine-api/types/filters"
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/docker/dockernetwork"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/go-logging"
	"github.com/pkg/errors"
	"io"
	"strings"
)

var c = lru.New(43690)

const defaultNetworkLabel = "dps.network"

func HandleDockerEvents(){

	// connecting to docker api
	ctx := reference.Context()
	cli, err := client.NewClient("unix:///var/run/docker.sock", "v1.21", nil, nil)
	if err != nil {
		logging.Warningf("status=error-parsing-host-url, err=%+v", err)
		return
	}
	serverVersion, err := cli.ServerVersion(ctx)
	if err != nil {
		logging.Warningf("status=error-to-connect-at-host, solver=docker, err=%v", err)
		return
	}
	logging.Infof("status=connected, serverVersion=%+v, err=%v", ctx, serverVersion, err)

	dockernetwork.SetCli(cli)

	// more about list containers https://docs.docker.com/engine/reference/commandline/ps/
	options := types.ContainerListOptions{}
	containers, err := cli.ContainerList(ctx, options)
	if err != nil {
		logging.Errorf("status=error-to-list-container, solver=docker, err=%v", ctx, err)
		return
	}

	if conf.DpsNetwork() && dockernetwork.IsDockerConnected() {
		setupDpsContainerNetwork(ctx)
	}

	// more about events here: http://docs-stage.docker.com/v1.10/engine/reference/commandline/events/
	var eventFilter = filters.NewArgs()
	eventFilter.Add("event", "start")
	eventFilter.Add("event", "die")
	eventFilter.Add("event", "stop")

	// registering at events before get the list of actual containers, this way no one container will be missed #55
	body, err := cli.Events(ctx, types.EventsOptions{Filters: eventFilter})
	if err != nil {
		logging.Errorf("status=error-to-attach-at-events-handler, solver=docker, err=%v", ctx, err)
		return
	}

	for _, c := range containers {

		dockernetwork.MustNetworkConnect(ctx, dockernetwork.DpsNetwork, c.ID, "")
		cInspection := mustInspectContainer(ctx, c.ID)
		hostnames := getHostnames(cInspection)
		putHostnames(ctx, hostnames, cInspection)

		logging.Infof("status=started-container-processed, container=%s, hostnames=%s", ctx, cInspection.Name, hostnames)
	}

	dec := json.NewDecoder(body)
	for {

		ctx := reference.Context()

		var event events.Message
		err := dec.Decode(&event)
		if err != nil && err == io.EOF {
			break
		}

		cInspection := mustInspectContainer(ctx, event.ID)
		hostnames := getHostnames(cInspection)

		action := event.Action
		if len(action) == 0 {
			action = event.Status
		}
		logging.Infof("status=resolved-hosts, action=%s, hostnames=%s", ctx, action, hostnames)
		switch action {
		case "start":
			dockernetwork.MustNetworkConnect(ctx, dockernetwork.DpsNetwork, cInspection.ID, "")
			putHostnames(ctx, hostnames, mustInspectContainer(ctx, event.ID))
			break

		case "stop", "die":
			for _, host := range hostnames {
				c.Remove(host)
			}
			break
		}
	}

}

func mustInspectContainer(ctx context.Context, containerID string) types.ContainerJSON {
	if cInspection, err := dockernetwork.GetCli().ContainerInspect(ctx, containerID); err != nil {
		panic(errors.WithMessage(err, fmt.Sprintf("status=inspect-error, id=%s", containerID)))
	} else {
		return cInspection
	}
}

func setupDpsContainerNetwork(ctx context.Context) {
	if _, err := dockernetwork.CreateOrUpdateDpsNetwork(ctx); err != nil {
		// todo disable dpsNetwork option here
		panic(fmt.Sprintf("can't create dps network %+v", err))
	}
	if dpsContainer, err := dockernetwork.FindDpsContainer(ctx); err != nil {
		logging.Infof("status=can't-find-dps-container, err=%+v", ctx, err.Error())
	} else {
		dpsContainerIP := "172.157.5.249"
		dockernetwork.MustNetworkDisconnectForIp(ctx, dockernetwork.DpsNetwork, dpsContainerIP)
		dockernetwork.MustNetworkConnect(ctx, dockernetwork.DpsNetwork, dpsContainer.ID, dpsContainerIP)
	}
}

func GetCache() cache.Cache {
	return c
}

// retrieve hostnames which should be registered given the container
func getHostnames(inspect types.ContainerJSON) []string {
	hostnames := *new([]string)
	if machineHostname, err := getContainerHostname(inspect); err == nil {
		hostnames = append(hostnames, machineHostname)
	}
	hostnames = append(hostnames, getHostnamesFromEnv(inspect)...)

	if conf.ShouldRegisterContainerNames() {
		hostnames = append(hostnames, getHostnameFromContainerName(inspect))
		if hostnameFromServiceName, err := getHostnameFromServiceName(inspect); err == nil {
			hostnames = append(hostnames, hostnameFromServiceName)
		}
	}
	return hostnames
}

func getHostnamesFromEnv(inspect types.ContainerJSON) ([]string){
	const hostnameEnv = "HOSTNAMES="
	hostnames := *new([]string)
	for _, env := range inspect.Config.Env {
		envName := strings.Index(env, hostnameEnv)
		if envName == 0 {
			envValue := env[envName + len(hostnameEnv) : ]
			hostnames = append(hostnames, strings.Split(envValue, ",")...)
			return hostnames
		}
	}
	return hostnames
}

// Returns current docker container machine hostname
func getContainerHostname(inspect types.ContainerJSON) (string, error) {
	if len(inspect.Config.Hostname) != 0 {
		if len(inspect.Config.Domainname) != 0 {
			return fmt.Sprintf("%s.%s", inspect.Config.Hostname, inspect.Config.Domainname), nil
		}else {
			return inspect.Config.Hostname, nil
		}
	}
	return "", errors.New("hostname not found")
}

func getHostnameFromContainerName(inspect types.ContainerJSON) string {
	return fmt.Sprintf("%s.%s", inspect.Name[1:], conf.GetDpsDomain())
}

func getHostnameFromServiceName(inspect types.ContainerJSON) (string, error) {
	const serviceNameLabelKey = "com.docker.compose.service"
	if v, ok := inspect.Config.Labels[serviceNameLabelKey]; ok {
		logging.Debugf("status=service-found, service=%s", v)
		return fmt.Sprintf("%s.docker", v), nil
	}
	return "", errors.New("service not found for container: " + inspect.Name)
}

func putHostnames(ctx context.Context, predefinedHosts []string, inspect types.ContainerJSON) {
	preferredNetwork := inspect.Config.Labels[defaultNetworkLabel]
	ip := dockernetwork.FindBestIPForNetworks(inspect, preferredNetwork, dockernetwork.DpsNetwork, "bridge")
	for _, host := range predefinedHosts {
		logging.Debugf("host=%s, ip=%s, container=%s, preferredNetworkName=%s", ctx, host, ip, inspect.Name, preferredNetwork)
		c.Put(host, ip)
	}
}
