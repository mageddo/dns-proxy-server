package docker

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/docker/engine-api/client"
	"github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/events"
	"github.com/docker/engine-api/types/filters"
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/lru"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/go-logging"
	"golang.org/x/net/context"
	"io"
	"strings"
)

var c = lru.New(43690)

const defaultNetworkLabel = "dps.network"

func HandleDockerEvents(){

	// connecting to docker api
	cli, err := client.NewClient("unix:///var/run/docker.sock", "v1.21", nil, nil)
	if err != nil {
		logging.Errorf("status=error-to-connect-at-host, solver=docker, err=%v", err)
		return
	}

	// more about list containers https://docs.docker.com/engine/reference/commandline/ps/
	options := types.ContainerListOptions{}
	ctx := context.Background()

	serverVersion, err := cli.ServerVersion(ctx)
	logging.Infof("serverVersion=%+v, err=%v", serverVersion, err)

	containers, err := cli.ContainerList(ctx, options)
	if err != nil {
		logging.Errorf("status=error-to-list-container, solver=docker, err=%v", err)
		return
	}

	// more about events here: http://docs-stage.docker.com/v1.10/engine/reference/commandline/events/
	var eventFilter = filters.NewArgs()
	eventFilter.Add("event", "start")
	eventFilter.Add("event", "die")
	eventFilter.Add("event", "stop")

	// registering at events before get the list of actual containers, this way no one container will be missed #55
	body, err := cli.Events(ctx, types.EventsOptions{ Filters: eventFilter })
	if err != nil {
		logging.Errorf("status=error-to-attach-at-events-handler, solver=docker, err=%v", err)
		return
	}

	for _, c := range containers {

		cInspection, err := cli.ContainerInspect(ctx, c.ID)
		logging.Infof("status=container-from-list-begin, container=%s", cInspection.Name)
		if err != nil {
			logging.Errorf("status=inspect-error-at-list, container=%s, err=%v", c.Names, err)
		}
		hostnames := getHostnames(cInspection)
		putHostnames(hostnames, cInspection)
		logging.Infof("status=container-from-list-success, container=%s, hostnames=%s", cInspection.Name, hostnames)

	}
	
	dec := json.NewDecoder(body)
	for {

		ctx := context.Background()

		var event events.Message
		err := dec.Decode(&event)
		if err != nil && err == io.EOF {
			break
		}

		cInspection, err := cli.ContainerInspect(ctx, event.ID)
		if err != nil {
			logging.Warningf("status=inspect-error, id=%s, err=%v", event.ID, err)
			continue
		}
		hostnames := getHostnames(cInspection)
		action := event.Action
		if len(action) == 0 {
			action = event.Status
		}
		logging.Infof("status=resolved-hosts, action=%s, hostnames=%s", action, hostnames)

		switch action {
		case "start":
			putHostnames(hostnames, cInspection)
			break

		case "stop", "die":
			for _, host := range hostnames {
				c.Remove(host)
			}
			break

		}
	}

}

func GetCache() cache.Cache {
	return c
}

func getHostnames(inspect types.ContainerJSON) []string {
	hostnames := *new([]string)
	if machineHostname, err := getMachineHostname(inspect); err == nil {
		hostnames = append(hostnames, machineHostname)
	}
	hostnames = append(hostnames, getHostnamesFromEnv(inspect)...)

	if conf.RegisterContainerNames() {
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
func getMachineHostname(inspect types.ContainerJSON) (string, error) {
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
	return fmt.Sprintf("%s.docker", inspect.Name[1:])
}

func getHostnameFromServiceName(inspect types.ContainerJSON) (string, error) {
	const serviceNameLabelKey = "com.docker.compose.service"
	if v, ok := inspect.Config.Labels[serviceNameLabelKey]; ok {
		logging.Debugf("status=service-found, service=%s", v)
		return fmt.Sprintf("%s.docker", v), nil
	}
	return "", errors.New("service not found")
}

func putHostnames(hostnames []string, inspect types.ContainerJSON) error {
	for _, host := range hostnames {
		networkName := inspect.Config.Labels[defaultNetworkLabel]
		ip := ""
		for actualNetwork, network := range inspect.NetworkSettings.Networks {
			logging.Debugf("container=%s, defaultNetwork=%s, network=%s, ip=%s", inspect.Name, networkName, actualNetwork, network.IPAddress)
			if len(networkName) == 0 || networkName == actualNetwork {
				ip = network.IPAddress
				break
			}
		}
		if len(ip) == 0 {
			ip = inspect.NetworkSettings.IPAddress
			if len(ip) == 0 {
				err := fmt.Sprintf("no network found to %s", inspect.Name)
				logging.Error(err)
				return errors.New(err)
			}
		}
		logging.Debugf("host=%s, ip=%s", host, ip)
		c.Put(host, ip)
	}
	return nil
}
