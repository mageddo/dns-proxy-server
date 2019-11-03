package storagev2

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
)

type ConfigurationV2 struct {
	Version int64 `json:"version"`
	/**
	 * The remote servers to ask when, DPS can not solve from docker or local file,
	 * it will try one by one in order, if no one is specified then 8.8.8.8 is used by default
	 * DO NOT call this variable directly, use GetRemoteDnsServers instead
	 */
	RemoteDnsServers []string `json:"remoteDnsServers"`
	Envs []EnvV2               `json:"envs"`
	ActiveEnv string           `json:"activeEnv"`

	/// ----
	WebServerPort int `json:"webServerPort"`
	DnsServerPort int `json:"dnsServerPort"`
	DefaultDns *bool `json:"defaultDns"`
	LogLevel string `json:"logLevel"`
	LogFile string `json:"logFile"`
	RegisterContainerNames *bool `json:"registerContainerNames"`

	// hostname to solve host machine IP
	HostMachineHostname string `json:"hostMachineHostname"`

	// domain utilized to solve container names
	Domain string `json:"domain"`

	DpsNetwork *bool `json:"dpsNetwork"`
	DpsNetworkAutoConnect *bool `json:"dpsNetworkAutoConnect"`
}

type EnvV2 struct {
	Name string            `json:"name"`
	Hostnames []HostnameV2 `json:"hostnames,omitempty"`
}

type HostnameV2 struct {
	Id int64 `json:"id"`
	Hostname string `json:"hostname"`
	Ip string `json:"ip"` // hostname ip when type=A
	Target string `json:"target"` // target hostname when type=CNAME
	Ttl int `json:"ttl"`
	Type localvo.EntryType `json:"type"`
}


func ValueOf(c *localvo.Configuration) *ConfigurationV2 {
	return &ConfigurationV2{
		Version:                int64(2),
		LogFile:                c.LogFile,
		ActiveEnv:              c.ActiveEnv,
		DefaultDns:             c.DefaultDns,
		DnsServerPort:          c.DnsServerPort,
		Domain:                 c.Domain,
		HostMachineHostname:    c.HostMachineHostname,
		LogLevel:               c.LogLevel,
		RegisterContainerNames: c.RegisterContainerNames,
		RemoteDnsServers:       localvo.ToIpsStringArray(c.RemoteDnsServers),
		WebServerPort:          c.WebServerPort,
		Envs:                   toV2Envs(c.Envs),
		DpsNetwork:             c.DpsNetwork,
		DpsNetworkAutoConnect:  c.DpsNetworkAutoConnect,
	}
}

func toV2Envs(envs []localvo.Env) []EnvV2 {
	v2Envs := make([]EnvV2, len(envs))
	for i, env := range envs {
		v2Envs[i] = toV2Env(env)
	}
	return v2Envs
}

func toV2Env(env localvo.Env) EnvV2 {
	return EnvV2{
		Hostnames: toV2Hostnames(env.Hostnames),
		Name:env.Name,
	}
}

func toV2Hostnames(hostnames []localvo.Hostname) []HostnameV2 {
	v2Hostnames := make([]HostnameV2, len(hostnames))
	for i, hostname := range hostnames {
		fillV2Hostname(&v2Hostnames[i], hostname)
	}
	return v2Hostnames
}

func toV2Hostname(hostname localvo.Hostname) HostnameV2 {
	hostnameV2 := &HostnameV2{}
	fillV2Hostname(hostnameV2, hostname)
	return *hostnameV2
}

func fillV2Hostname(hostnameV2 *HostnameV2, hostname localvo.Hostname) {
	hostnameV2.Hostname = hostname.Hostname
	hostnameV2.Ip = hostname.Ip
	hostnameV2.Id = hostname.Id
	hostnameV2.Target = hostname.Target
	hostnameV2.Ttl = hostname.Ttl
	hostnameV2.Type = hostname.Type
}

func (c *ConfigurationV2) ToConfig() *localvo.Configuration {
	return &localvo.Configuration{
		Version:                2,
		ActiveEnv:              c.ActiveEnv,
		DefaultDns:             c.DefaultDns,
		DnsServerPort:          c.DnsServerPort,
		Domain:                 c.Domain,
		Envs:                   toEnvs(c.Envs),
		HostMachineHostname:    c.HostMachineHostname,
		LogFile:                c.LogFile,
		LogLevel:               c.LogLevel,
		RegisterContainerNames: c.RegisterContainerNames,
		RemoteDnsServers:       localvo.StringArrayToDnsServer(c.RemoteDnsServers),
		WebServerPort:          c.WebServerPort,
		DpsNetwork:             c.DpsNetwork,
		DpsNetworkAutoConnect:  c.DpsNetworkAutoConnect,
	}
}

func toEnvs(v2Envs []EnvV2) []localvo.Env {
	envs := make([]localvo.Env, len(v2Envs))
	for i, v2Env := range v2Envs {
		env := &envs[i]
		env.Hostnames = make([]localvo.Hostname, len(v2Env.Hostnames))
		env.Name = v2Env.Name
		for j, v2Hostname := range v2Env.Hostnames {
			fillHostname(&env.Hostnames[j], &v2Hostname)
		}
	}
	return envs
}

func fillHostname(hostname *localvo.Hostname, v2Hostname *HostnameV2) {
	hostname.Hostname = v2Hostname.Hostname
	hostname.Id = v2Hostname.Id
	hostname.Ip = v2Hostname.Ip
	hostname.Target = v2Hostname.Target
	hostname.Ttl = v2Hostname.Ttl
	hostname.Type = v2Hostname.Type
}
