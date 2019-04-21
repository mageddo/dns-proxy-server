package localvo

import (
	"context"
	"errors"
	"fmt"
	"github.com/mageddo/go-logging"
	"regexp"
)

type Configuration struct {
	Version int
	/**
	 * The remote servers to ask when, DPS can not solve from docker or local file,
	 * it will try one by one in order, if no one is specified then 8.8.8.8 is used by default
	 * DO NOT call this variable directly, use GetRemoteDnsServers instead
	 */
	RemoteDnsServers [][4]byte
	Envs []Env
	ActiveEnv string
	WebServerPort int
	DnsServerPort int
	DefaultDns *bool
	LogLevel string
	LogFile string
	RegisterContainerNames *bool

	// hostname to solve host machine IP
	HostMachineHostname string

	// domain utilized to solve container names
	Domain string
}

type Env struct {
	Name string
	Hostnames []Hostname
}

type EntryType string
const (
	A EntryType = "A"
	CNAME EntryType = "CNAME"
)

type Hostname struct {
	Id       int64
	Hostname string
	Ip       [4]byte // hostname ip when type=A
	Target   string  // target hostname when type=CNAME
	Ttl      int
	Type     EntryType
}

func (lc *Configuration) GetEnv(envName string) (*Env, int) {
	for i := range lc.Envs {
		env := &lc.Envs[i]
		if (*env).Name == envName {
			return env, i
		}
	}
	return nil, -1
}

func (env *Env) AddHostname(hostname *Hostname) error {

	logging.Infof("status=begin, env=%s, hostname=%+v", env.Name, hostname)
	if env == nil {
		return errors.New("env not found")
	}
	foundHost, _ := env.GetHostname(hostname.Hostname)
	if foundHost != nil {
		return errors.New(fmt.Sprintf("The host '%s' already exists", hostname.Hostname))
	}

	(*env).Hostnames = append(env.Hostnames, *hostname)
	logging.Infof("status=success, foundEnv=%s", env.Name)
	return nil
}

func (lc *Configuration) GetActiveEnv() (*Env, int) {
	return lc.GetEnv(lc.ActiveEnv)
}

func(env *Env) GetHostname(hostname string) (*Hostname, int) {
	for i := range env.Hostnames {
		host := &env.Hostnames[i]
		if (*host).Hostname == hostname {
			logging.Debugf("status=hostname-found, env=%s, hostname=%s", env.Name, hostname)
			return host, i
		}
	}
	logging.Debugf("status=hostname-not-found, env=%s, hostname=%s", env.Name, hostname)
	return nil, -1
}

func(env *Env) FindHostnameByName(ctx context.Context, hostname string) *[]Hostname {
	logging.Infof("status=begin, hostname=%s", hostname)
	hostList := []Hostname{}
	for _, host := range env.Hostnames {
		if matched, _ := regexp.MatchString(fmt.Sprintf(".*%s.*", hostname), host.Hostname); matched {
			hostList = append(hostList, host)
		}
	}
	logging.Infof("status=success, hostname=%s, length=%d", hostname, len(hostList))
	return &hostList
}

func(lc *Configuration) FindHostnameByNameAndEnv(ctx context.Context, envName, hostname string) (*[]Hostname, error) {
	logging.Infof("status=begin, envName=%s, hostname=%s", envName, hostname)
	env,_ := lc.GetEnv(envName)
	if env == nil {
		return nil, errors.New("env not found")
	}
	logging.Infof("status=success, envName=%s, hostname=%s", envName, hostname)
	return env.FindHostnameByName(ctx, hostname), nil
}

func(env *Env) GetHostnameByName(name string) (*Hostname, int) {
	for i := range env.Hostnames {
		host := &env.Hostnames[i]
		if (*host).Hostname == name {
			return host, i
		}
	}
	return nil, -1
}

func (lc *Configuration) AddEnv(ctx context.Context, env Env) error {
	logging.Infof("status=begin, env=%s", env.Name)
	foundEnv, _ := lc.GetEnv(env.Name)
	if foundEnv != nil {
		return errors.New(fmt.Sprintf("The '%s' env already exists", env.Name))
	}
	lc.Envs = append(lc.Envs, env)
	logging.Infof("status=success, env=%s", env.Name)
	return nil
}

func (lc *Configuration) RemoveEnvByName(ctx context.Context, name string) error {
	logging.Infof("status=begin, env=%s", ctx, name)
	env, i := lc.GetEnv(name)
	if env == nil {
		return errors.New(fmt.Sprintf("The env '%s' was not found", name))
	}
	lc.RemoveEnv(i)
	logging.Infof("status=success, env=%s", ctx, name)
	return nil
}

func (lc *Configuration) RemoveEnv(index int){
	logging.Infof("status=begin, index=%d", index)
	lc.Envs = append(lc.Envs[:index], lc.Envs[index+1:]...)
	logging.Infof("status=success, index=%d", index)
}

func (lc *Configuration) AddDns( dns [4]byte){
	lc.RemoteDnsServers = append(lc.RemoteDnsServers, dns)
}

func (lc *Configuration) RemoveDns(index int){
	lc.RemoteDnsServers = append(lc.RemoteDnsServers[:index], lc.RemoteDnsServers[index+1:]...)
}

func (lc *Configuration) AddHostname(envName string, hostname Hostname) error {
	if hostname.Type == "" {
		return errors.New("Type is required")
	}
	logging.Infof("status=begin, evnName=%s, hostname=%+v", envName, hostname)
	foundEnv, _ := lc.GetEnv(envName)
	if foundEnv == nil {
		return errors.New("env not found")
	}
	err := foundEnv.AddHostname(&hostname)
	if err != nil {
		return err
	}
	logging.Infof("status=success")
	return nil
}

func (lc *Configuration) UpdateHostname(envName string, hostname Hostname) error {
	logging.Infof("status=begin, evnName=%s, hostname=%+v", envName, hostname)
	env, _ := lc.GetEnv(envName)
	if env == nil {
		return errors.New("env not found")
	}

	err := env.UpdateHostname(hostname)
	if err != nil {
		return err
	}
	logging.Infof("status=success, hostname=%s", hostname.Hostname)
	return nil
}

func (env *Env) UpdateHostname(hostname Hostname) error {

	foundHostname, _ := env.GetHostnameByName(hostname.Hostname)
	if foundHostname == nil {
		return errors.New("not hostname found: " + hostname.Hostname)
	}
	foundHostname.Hostname = hostname.Hostname
	foundHostname.Ttl = hostname.Ttl
	foundHostname.Ip = hostname.Ip
	foundHostname.Target = hostname.Target
	foundHostname.Type = hostname.Type
	return nil
}

func (lc *Configuration) RemoveHostnameByEnvAndHostname(envName string, hostname string) error {
	logging.Infof("status=begin, envName=%s, hostname=%s", envName, hostname)
	env, envIndex := lc.GetEnv(envName)
	if envIndex == -1 {
		return errors.New("env not found")
	}
	host, hostIndex := env.GetHostname(hostname)
	if host == nil {
		return errors.New("hostname not found")
	}
	lc.RemoveHostname(envIndex, hostIndex)
	logging.Infof("status=success, envName=%s, hostname=%s", envName, hostname)
	return nil
}

func (lc *Configuration) RemoveHostname(envIndex int, hostIndex int){

	logging.Infof("status=begin, envIndex=%d, hostIndex=%d", envIndex, hostIndex)
	env := &lc.Envs[envIndex]
	(*env).Hostnames = append((*env).Hostnames[:hostIndex], (*env).Hostnames[hostIndex+1:]...)
	logging.Infof("status=success, envIndex=%d, hostIndex=%d", envIndex, hostIndex)

}

func (lc *Configuration) SetActiveEnv(env Env) error {
	foundEnv, _ := lc.GetEnv(env.Name)
	if foundEnv == nil {
		logging.Warningf("status=env-not-found, envName=%s", env.Name)
		return errors.New("Env not found: " + env.Name)
	}
	lc.ActiveEnv = env.Name
	logging.Infof("status=success, activeEnv=%s", env.Name)
	return nil
}

func (lc *Configuration) GetRemoteServers(ctx context.Context) [][4]byte {
	if len(lc.RemoteDnsServers) == 0 {
		lc.RemoteDnsServers = append(lc.RemoteDnsServers, [4]byte{8, 8, 8, 8})
		logging.Infof("status=put-default-server")
	}
	return lc.RemoteDnsServers
}

