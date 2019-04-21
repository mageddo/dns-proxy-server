package storagev1

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
)

type ConfigurationV1 struct {
	/**
	 * The remote servers to ask when, DPS can not solve from docker or local file,
	 * it will try one by one in order, if no one is specified then 8.8.8.8 is used by default
	 * DO NOT call this variable directly, use GetRemoteDnsServers instead
	 */
	RemoteDnsServers [][4]byte `json:"remoteDnsServers"`
	Envs []EnvV1               `json:"envs"`
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
}

type EnvV1 struct {
	Name string            `json:"name"`
	Hostnames []HostnameV1 `json:"hostnames,omitempty"`
}

type HostnameV1 struct {
	Hostname string `json:"hostname"`
	Ip [4]byte `json:"ip"` // hostname ip when type=A
	Target string `json:"target"` // target hostname when type=CNAME
	Ttl int `json:"ttl"`
	Env string `json:"env,omitempty"` // apenas para o post do rest,
	Type localvo.EntryType `json:"type"`
}


func ValueOf(c *localvo.Configuration) *ConfigurationV1 {
	return &ConfigurationV1{
		Envs: toV1Envs(c.Envs),
		WebServerPort:c.WebServerPort,
		RemoteDnsServers:c.RemoteDnsServers,
		RegisterContainerNames:c.RegisterContainerNames,
		LogLevel:c.LogLevel,
		LogFile:c.LogFile,
		HostMachineHostname:c.HostMachineHostname,
		Domain:c.Domain,
		DnsServerPort:c.DnsServerPort,
		DefaultDns:c.DefaultDns,
		ActiveEnv:c.ActiveEnv,
	}
}

func toV1Envs(envs []localvo.Env) []EnvV1 {
	v1Envs := make([]EnvV1, len(envs))
	for i, env := range envs {
		v1Envs[i] = fromEnv(env)
	}
	return v1Envs
}

func fromEnv(env localvo.Env) EnvV1 {
	return EnvV1{
		Name:env.Name,
		Hostnames: toV1Hostnames(env.Hostnames),
	}
}

func toV1Hostnames(hostnames []localvo.Hostname) []HostnameV1 {
	v1Hostnames := make([]HostnameV1, len(hostnames))
	for i, hostname := range hostnames {
		v1Hostnames[i] = fromHostname(hostname)
	}
	return v1Hostnames
}

func fromHostname(hostname localvo.Hostname) HostnameV1 {
	return HostnameV1{
		Hostname:hostname.Hostname,
		Env:hostname.Env,
		Type:hostname.Type,
		Ttl:hostname.Ttl,
		Target:hostname.Target,
		Ip:hostname.Ip,
	}
}

func (c *ConfigurationV1) ToConfig() *localvo.Configuration {
	return &localvo.Configuration{
		Version:1,
		ActiveEnv:c.ActiveEnv,
		DefaultDns:c.DefaultDns,
		DnsServerPort:c.DnsServerPort,
		Domain:c.Domain,
		Envs: toEnvs(c.Envs),
		HostMachineHostname:c.HostMachineHostname,
		LogFile: c.LogFile,
		LogLevel:c.LogLevel,
		RegisterContainerNames:c.RegisterContainerNames,
		RemoteDnsServers:c.RemoteDnsServers,
		WebServerPort:c.WebServerPort,
	}
}

func toEnvs(v1Envs []EnvV1) []localvo.Env {
	envs := make([]localvo.Env, len(v1Envs))
	for i := range envs {
		v1Env := v1Envs[i]
		env := &envs[i]
		env.Name = v1Env.Name
		for i := range env.Hostnames {
			fillHostname(&env.Hostnames[i], &v1Env.Hostnames[i])
		}
	}
	return envs
}

func fillHostname(hostname *localvo.Hostname, v1Hostname *HostnameV1) {
	hostname.Env = v1Hostname.Env
	hostname.Hostname = v1Hostname.Hostname
	hostname.Ip = v1Hostname.Ip
	hostname.Target = v1Hostname.Target
	hostname.Ttl = v1Hostname.Ttl
	hostname.Type = v1Hostname.Type
}
