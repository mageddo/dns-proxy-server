package storagev1

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
)

type LocalConfiguration struct {
	/**
	 * The remote servers to ask when, DPS can not solve from docker or local file,
	 * it will try one by one in order, if no one is specified then 8.8.8.8 is used by default
	 * DO NOT call this variable directly, use GetRemoteDnsServers instead
	 */
	RemoteDnsServers [][4]byte `json:"remoteDnsServers"`
	Envs []EnvVo `json:"envs"`
	ActiveEnv string `json:"activeEnv"`
	LastId int `json:"lastId"`

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

type EnvVo struct {
	Name string `json:"name"`
	Hostnames []HostnameVo `json:"hostnames,omitempty"`
}

type HostnameVo struct {
	Id int `json:"id"`
	Hostname string `json:"hostname"`
	Ip [4]byte `json:"ip"` // hostname ip when type=A
	Target string `json:"target"` // target hostname when type=CNAME
	Ttl int `json:"ttl"`
	Env string `json:"env,omitempty"` // apenas para o post do rest,
	Type localvo.EntryType `json:"type"`
}


func ValueOf(c *localvo.Configuration) *LocalConfiguration {
	panic("unsupported operation")
}

func (c *LocalConfiguration) ToConfig() *localvo.Configuration {
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

func toEnvs(v1Envs []EnvVo) []localvo.Env {
	envs := make([]localvo.Env, len(v1Envs))
	for i, env := range envs {
		v1Env := v1Envs[i]
		env.Name = v1Env.Name
		for i, hostname := range env.Hostnames {
			fillHostname(&hostname, &v1Env.Hostnames[i])
		}
	}
	return envs
}

func fillHostname(hostname *localvo.Hostname, v1Hostname *HostnameVo) {
	hostname.Env = v1Hostname.Env
	hostname.Hostname = v1Hostname.Hostname
	hostname.Id = v1Hostname.Id
	hostname.Ip = v1Hostname.Ip
	hostname.Target = v1Hostname.Target
	hostname.Ttl = v1Hostname.Ttl
	hostname.Type = v1Hostname.Type
}
