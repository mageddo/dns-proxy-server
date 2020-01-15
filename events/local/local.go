package local

import (
	"bufio"
	"context"
	"encoding/json"
	"errors"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"github.com/mageddo/dns-proxy-server/events/local/storagev1"
	"github.com/mageddo/dns-proxy-server/events/local/storagev2"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/go-logging"
	"io/ioutil"
	"os"
	"strings"
	"time"
)

var confPath = GetConfPath()

func GetConfPath() string {
	return utils.GetPath(*flags.ConfPath)
}

func LoadConfiguration() (*localvo.Configuration, error){
	logging.Debugf("status=loading, path=%s", confPath)
	if _, err := os.Stat(confPath); err == nil {
		confBytes, err := ioutil.ReadFile(confPath)
		if err != nil {
			logging.Error("status=can't-read-conf-file, file=%s", confPath)
			return nil, err
		}
		if configuration, err := LoadVersionedConfiguration(confBytes); err != nil {
			logging.Error("status=can't-read-conf-file-version, file=%s", confPath)
			return nil, err
		} else {
			setHostnameIds(configuration)
			return configuration, nil
		}
	} else {
		pfalse := false
		defaultConfig := &localvo.Configuration{
			Version:          2,
			Envs:             make([]localvo.Env, 0),
			RemoteDnsServers: make([]localvo.DNSServer, 0),
			DpsNetwork: &pfalse,
			DpsNetworkAutoConnect: &pfalse,
		}
		storeDefaultConfig(defaultConfig)
		return defaultConfig, nil
	}
}

func setHostnameIds(configuration *localvo.Configuration) {
	atLeastOneUpdated := false
	for _, env := range configuration.Envs {
		for i := range env.Hostnames {
			host := &env.Hostnames[i]
			if host.Id == 0 {
				logging.Infof("status=without-id, hostname=%s, id=%d", host.Hostname, host.Id)
				host.Id = time.Now().UnixNano()
				atLeastOneUpdated = true
			}
		}
	}
	if atLeastOneUpdated {
		SaveConfiguration(configuration)
	}
}

func LoadVersionedConfiguration(confBytes []byte) (*localvo.Configuration, error) {
	switch readVersion(confBytes) {
	case 1:
		v1Config := &storagev1.ConfigurationV1{
			Envs: make([]storagev1.EnvV1, 0),
			RemoteDnsServers: make([][4]byte, 0),
		}
		err := json.Unmarshal(confBytes, v1Config)
		return v1Config.ToConfig(), err
	case 2:
		v2Config := &storagev2.ConfigurationV2{
			Envs: make([]storagev2.EnvV2, 0),
			RemoteDnsServers: make([]string, 0),
		}
		err := json.Unmarshal(confBytes, v2Config)
		return v2Config.ToConfig(), err
	}
	return nil, errors.New("unrecognized version")
}

func readVersion(confBytes []byte) int {
	m := make(map[string]interface{})
	json.Unmarshal(confBytes, &m)
	version, found := m["version"]
	if found {
		return int(version.(float64))
	} else {
		return 1
	}
}

func SaveConfiguration(c *localvo.Configuration) {

	if len(c.Envs) == 0 {
		c.Envs = NewEmptyEnv()
	}

	var confVO interface{}
	switch c.Version {
	case 2:
		confVO = storagev2.ValueOf(c)
	default:
		confVO = storagev1.ValueOf(c)
	}
	storeToFile(confVO)
}

func storeDefaultConfig(configuration *localvo.Configuration) error {
	err := os.MkdirAll(confPath[:strings.LastIndex(confPath, "/")], 0755)
	if err != nil {
		logging.Errorf("status=error-to-create-conf-path, path=%s", confPath)
		return err
	}
	SaveConfiguration(configuration)
	logging.Info("status=success-creating-conf-file, path=%s", confPath)
	return nil
}

func NewEmptyEnv() []localvo.Env {
	return []localvo.Env{{Hostnames: []localvo.Hostname{}, Name:""}}
}

func ResetConf() {
	os.Remove(confPath)
	store.ClearAllCaches()
}

func storeToFile(confFileVO interface{}){
	now := time.Now()
	logging.Debugf("status=save, confPath=%s", confPath)
	f, err := os.OpenFile(confPath, os.O_RDWR|os.O_CREATE|os.O_TRUNC, 0777)
	if err != nil {
		logging.Errorf("status=error-to-create-conf-file, confPath=%s, err=%v", confPath, err)
		return
	}

	defer f.Close()
	wr := bufio.NewWriter(f)
	defer wr.Flush()
	enc := json.NewEncoder(wr)
	enc.SetIndent("", "\t")
	err = enc.Encode(confFileVO)
	if err != nil {
		logging.Errorf("status=error-to-encode, error=%v", err)
	}
	store.ClearAllCaches()
	logging.Infof("status=success, confPath=%s, time=%d", confPath, utils.DiffMillis(now, time.Now()))
}

func SetActiveEnv(env localvo.Env) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.SetActiveEnv(env); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}

func AddEnv(ctx context.Context, env localvo.Env) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.AddEnv(ctx, env); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}

func RemoveEnvByName(ctx context.Context, env string) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.RemoveEnvByName(ctx, env); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}

func RemoveHostnameByEnvAndHostname(env, hostname string) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.RemoveHostnameByEnvAndHostname(env, hostname); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}

func AddHostname(env string, hostname localvo.Hostname) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.AddHostname(env, hostname); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}

func UpdateHostname(env string, hostname localvo.Hostname) error {
	if conf, err := LoadConfiguration(); err == nil {
		if err := conf.UpdateHostname(env, hostname); err == nil {
			SaveConfiguration(conf)
			return nil
		} else {
			return err
		}
	} else {
		return err
	}
}
