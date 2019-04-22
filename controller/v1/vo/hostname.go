package vo

import (
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"strconv"
)

type HostnameV1 struct {
	Id string `json:"id"`
	Hostname string `json:"hostname"`
	Ip [4]byte `json:"ip"`
	Target string `json:"target"`
	Ttl int `json:"ttl"`
	Type localvo.EntryType `json:"type"`
	Env string `json:"env,omitempty"`
}

func (hostnameV1 HostnameV1) ToHostname() localvo.Hostname {
	return localvo.Hostname{
		Id: parseInt(hostnameV1.Id),
		Hostname:hostnameV1.Hostname,
		Ip:hostnameV1.Ip,
		Target:hostnameV1.Target,
		Ttl:hostnameV1.Ttl,
		Type:hostnameV1.Type,
	}
}

func parseInt(s string) int64 {
	id, _ := strconv.ParseInt(s, 10, 64)
	return id
}

func fromV1Hostnames(v1Hostnames []HostnameV1) []localvo.Hostname {
	hostnames := make([]localvo.Hostname, len(v1Hostnames))
	for i, hostnameV1 := range v1Hostnames {
		hostnames[i] = hostnameV1.ToHostname()
	}
	return hostnames
}


func FromHostnames(env string, hostnames []localvo.Hostname) []HostnameV1 {
	v1Hostnames := make([]HostnameV1, len(hostnames))
	for i, hostname := range hostnames {
		v1Hostnames[i] = fromHostname(env, hostname)
	}
	return v1Hostnames
}

func fromHostname(env string,hostname localvo.Hostname) HostnameV1 {
	return HostnameV1{
		Id:       strconv.FormatInt(hostname.Id, 10),
		Type:     hostname.Type,
		Ttl:      hostname.Ttl,
		Target:   hostname.Target,
		Ip:       hostname.Ip,
		Hostname: hostname.Hostname,
		Env:      env,
	}
}
