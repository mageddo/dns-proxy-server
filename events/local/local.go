package local

import (
	"bufio"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/go-logging"
	"golang.org/x/net/context"
	"os"
	"regexp"
	"strings"
	"time"
)

var confPath = GetConfPath()

func GetConfPath() string {
	return utils.GetPath(*flags.ConfPath)
}

func LoadConfiguration() (*LocalConfiguration, error){

	configuration := LocalConfiguration {
		Envs: make([]EnvVo, 0),
		RemoteDnsServers: make([][4]byte, 0),
	}

	if _, err := os.Stat(confPath); err == nil {

		f, _ := os.Open(confPath)

		defer f.Close()

		dec := json.NewDecoder(f)
		dec.Decode(&configuration)

		for i := range configuration.Envs {
			env := &configuration.Envs[i]
			for j := range env.Hostnames {
				host := &env.Hostnames[j]
				if host.Id <= 0 {
					logging.Infof("status=without-id, hostname=%s, id=%d", host.Hostname, host.Id)
					host.Id = configuration.nextId()
				}
			}
		}
		logging.Debugf("status=success-loaded-file, path=%s", confPath)
	} else {
		err := os.MkdirAll(confPath[:strings.LastIndex(confPath, "/")], 0755)
		if err != nil {
			logging.Errorf("status=error-to-create-conf-path, path=%s", confPath)
			return nil, err
		}
		SaveConfiguration(&configuration)
		logging.Info("status=success-creating-conf-file, path=%s", confPath)
	}
	return &configuration, nil
}

func SaveConfiguration(c *LocalConfiguration) {

	t := time.Now()
	logging.Debugf("status=begin")
	if len(c.Envs) == 0 {
		c.Envs = NewEmptyEnv()
	}

	logging.Debugf("status=save")
	f, err := os.OpenFile(confPath, os.O_RDWR|os.O_CREATE|os.O_TRUNC, 0777)
	if err != nil {
		logging.Errorf("status=error-to-create-conf-file, err=%v", err)
		return
	}
	defer f.Close()
	wr := bufio.NewWriter(f)
	defer wr.Flush()
	enc := json.NewEncoder(wr)
	enc.SetIndent("", "\t")
	err = enc.Encode(c)
	if err != nil {
		logging.Errorf("status=error-to-encode, error=%v", err)
	}
	store.GetInstance().Clear()
	logging.Infof("status=success, time=%d", utils.DiffMillis(t, time.Now()))

}

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

type EntryType string
const (
	A EntryType = "A"
	CNAME EntryType = "CNAME"
)

type HostnameVo struct {
	Id int `json:"id"`
	Hostname string `json:"hostname"`
	Ip [4]byte `json:"ip"` // hostname ip when type=A
	Target string `json:"target"` // target hostname when type=CNAME
	Ttl int `json:"ttl"`
	Env string `json:"env,omitempty"` // apenas para o post do rest,
	Type EntryType `json:"type"`
}

func (lc *LocalConfiguration) GetEnv(envName string) (*EnvVo, int) {
	for i := range lc.Envs {
		env := &lc.Envs[i]
		if (*env).Name == envName {
			return env, i
		}
	}
	return nil, -1
}

func (foundEnv *EnvVo) AddHostname(hostname *HostnameVo) error {

	logging.Infof("status=begin, env=%s, hostname=%+v", foundEnv.Name, hostname)
	if foundEnv == nil {
		return errors.New("env not found")
	}
	foundHost, _ := foundEnv.GetHostname(hostname.Hostname)
	if foundHost != nil {
		return errors.New(fmt.Sprintf("The host '%s' already exists", hostname.Hostname))
	}

	(*foundEnv).Hostnames = append(foundEnv.Hostnames, *hostname)
	logging.Infof("status=success, foundEnv=%s", foundEnv.Name)
	return nil
}

func (lc *LocalConfiguration) GetActiveEnv() (*EnvVo, int) {
	return lc.GetEnv(lc.ActiveEnv)
}

func(env *EnvVo) GetHostname(hostname string) (*HostnameVo, int) {
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

func(env *EnvVo) FindHostnameByName(ctx context.Context, hostname string) *[]HostnameVo {
	logging.Infof("status=begin, hostname=%s", hostname)
	hostList := []HostnameVo{}
	for _, host := range env.Hostnames {
		if matched, _ := regexp.MatchString(fmt.Sprintf(".*%s.*", hostname), host.Hostname); matched {
			hostList = append(hostList, host)
		}
	}
	logging.Infof("status=success, hostname=%s, length=%d", hostname, len(hostList))
	return &hostList
}

func(lc *LocalConfiguration) FindHostnameByNameAndEnv(ctx context.Context, envName, hostname string) (*[]HostnameVo, error) {
	logging.Infof("status=begin, envName=%s, hostname=%s", envName, hostname)
	env,_ := lc.GetEnv(envName)
	if env == nil {
		return nil, errors.New("env not found")
	}
	logging.Infof("status=success, envName=%s, hostname=%s", envName, hostname)
	return env.FindHostnameByName(ctx, hostname), nil
}

func(env *EnvVo) GetHostnameById(id int) (*HostnameVo, int) {
	for i := range env.Hostnames {
		host := &env.Hostnames[i]
		if (*host).Id == id {
			return host, i
		}
	}
	return nil, -1
}

func (lc *LocalConfiguration) AddEnv(ctx context.Context, env EnvVo) error {
	logging.Infof("status=begin, env=%s", env.Name)
	foundEnv, _ := lc.GetEnv(env.Name)
	if foundEnv != nil {
		return errors.New(fmt.Sprintf("The '%s' env already exists", env.Name))
	}
	lc.Envs = append(lc.Envs, env)
	SaveConfiguration(lc)
	logging.Infof("status=success, env=%s", env.Name)
	return nil
}

func (lc *LocalConfiguration) RemoveEnvByName(name string) error {
	logging.Infof("status=begin, env=%s", name)
	env, i := lc.GetEnv(name)
	if env == nil {
		return errors.New(fmt.Sprintf("The env '%s' was not found", name))
	}
	lc.RemoveEnv(i)
	SaveConfiguration(lc)
	logging.Infof("status=success, env=%s", name)
	return nil
}

func (lc *LocalConfiguration) RemoveEnv(index int){
	logging.Infof("status=begin, index=%d", index)
	lc.Envs = append(lc.Envs[:index], lc.Envs[index+1:]...)
	SaveConfiguration(lc)
	logging.Infof("status=success, index=%d", index)
}

func (lc *LocalConfiguration) AddDns( dns [4]byte){
	lc.RemoteDnsServers = append(lc.RemoteDnsServers, dns)
	SaveConfiguration(lc)
}

func (lc *LocalConfiguration) RemoveDns(index int){
	lc.RemoteDnsServers = append(lc.RemoteDnsServers[:index], lc.RemoteDnsServers[index+1:]...)
	SaveConfiguration(lc)
}


func (lc *LocalConfiguration) AddHostname(envName string, hostname HostnameVo) error {
	if hostname.Type == "" {
		return errors.New("Type is required")
	}
	hostname.Id = lc.nextId()
	logging.Infof("status=begin, evnName=%s, hostname=%+v", envName, hostname)
	foundEnv, _ := lc.GetEnv(envName)
	if foundEnv == nil {
		return errors.New("env not found")
	}
	err := foundEnv.AddHostname(&hostname)
	if err != nil {
		return err
	}
	SaveConfiguration(lc)
	logging.Infof("status=success")
	return nil
}

func (lc *LocalConfiguration) nextId() int {
	lc.LastId++
	return lc.LastId
}

func (lc *LocalConfiguration) UpdateHostname(envName string, hostname HostnameVo) error {
	logging.Infof("status=begin, evnName=%s, hostname=%+v", envName, hostname)
	env, _ := lc.GetEnv(envName)
	if env == nil {
		return errors.New("env not found")
	}

	err := env.UpdateHostname(hostname)
	if err != nil {
		return err
	}

	SaveConfiguration(lc)
	logging.Infof("status=success, hostname=%s", hostname.Hostname)
	return nil
}

func (env *EnvVo) UpdateHostname(hostname HostnameVo) error {

	foundHostname, _ := env.GetHostnameById(hostname.Id)
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

func (lc *LocalConfiguration) RemoveHostnameByEnvAndHostname(envName string, hostname string) error {
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

func (lc *LocalConfiguration) RemoveHostname(envIndex int, hostIndex int){

	logging.Infof("status=begin, envIndex=%d, hostIndex=%d", envIndex, hostIndex)
	env := &lc.Envs[envIndex]
	(*env).Hostnames = append((*env).Hostnames[:hostIndex], (*env).Hostnames[hostIndex+1:]...)
	SaveConfiguration(lc)
	logging.Infof("status=success, envIndex=%d, hostIndex=%d", envIndex, hostIndex)

}

func (lc *LocalConfiguration) SetActiveEnv(env EnvVo) error {
	logging.Infof("status=begin, envActive=%s", env.Name)
	foundEnv, _ := lc.GetEnv(env.Name)
	if foundEnv == nil {
		logging.Warningf("status=env-not-found, envName=%s", env.Name)
		return errors.New("Env not found: " + env.Name)
	}
	lc.ActiveEnv = env.Name
	SaveConfiguration(lc)
	logging.Infof("status=success")
	return nil
}

func NewEmptyEnv() []EnvVo {
	return []EnvVo{{Hostnames:[]HostnameVo{}, Name:""}}
}

func (lc *LocalConfiguration) GetRemoteServers(ctx context.Context) [][4]byte {
	if len(lc.RemoteDnsServers) == 0 {
		lc.RemoteDnsServers = append(lc.RemoteDnsServers, [4]byte{8, 8, 8, 8})
		logging.Infof("status=put-default-server")
	}
	return lc.RemoteDnsServers
}

func ResetConf() {
	if err := os.Remove(confPath); err != nil {
		logging.Errorf("reset=failed, err=%v", err)
		os.Exit(-1)
	}
	store.GetInstance().Clear()
}
