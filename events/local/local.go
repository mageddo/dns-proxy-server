package local

import (
	"bufio"
	"encoding/json"
	"errors"
	"github.com/mageddo/dns-proxy-server/cache/store"
	v1 "github.com/mageddo/dns-proxy-server/events/local/storage/v1"
	"github.com/mageddo/dns-proxy-server/events/local/storage/v2"
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

func LoadConfiguration() (*LocalConfiguration, error){
	if _, err := os.Stat(confPath); err == nil {
		confBytes, err := ioutil.ReadFile(confPath)
		if err != nil {
			return nil, err
		}
		switch readVersion(confBytes) {
		case 1:
			v1Config := &v1.LocalConfiguration {
				Envs: make([]v1.EnvVo, 0),
				RemoteDnsServers: make([][4]byte, 0),
			}
			err := json.Unmarshal(confBytes, v1Config)
			return v1Config.ToConfig(), err
		case 2:
			v2Config := &v2.LocalConfiguration {
				Envs: make([]v2.EnvVo, 0),
				RemoteDnsServers: make([][4]byte, 0),
			}
			err := json.Unmarshal(confBytes, v2Config)
			return v2Config.ToConfig(), err
		}
		logging.Debugf("status=success-loaded-file, path=%s", confPath)
	} else {
		storeDefaultConfig(&LocalConfiguration{
			Version:1,
			Envs:             make([]EnvVo, 0),
			RemoteDnsServers: make([][4]byte, 0),
		})
	}
	return nil, errors.New("unrecognized version")
}

func readVersion(confBytes []byte) int {
	m := make(map[string]interface{})
	json.Unmarshal(confBytes, m)
	version, found := m["version"]
	if found {
		return version.(int)
	} else {
		return 1
	}
}

func SaveConfiguration(c *LocalConfiguration) {

	if len(c.Envs) == 0 {
		c.Envs = NewEmptyEnv()
	}

	var confVO interface{}
	switch c.Version {
	case 2:
		confVO = v2.ValueOf(c)
	default:
		confVO = v1.ValueOf(c)
	}
	storeToFile(confVO)
}

func storeDefaultConfig(configuration *LocalConfiguration) error {
	err := os.MkdirAll(confPath[:strings.LastIndex(confPath, "/")], 0755)
	if err != nil {
		logging.Errorf("status=error-to-create-conf-path, path=%s", confPath)
		return err
	}
	SaveConfiguration(configuration)
	logging.Info("status=success-creating-conf-file, path=%s", confPath)
	return nil
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
	store.GetInstance().Clear()
	logging.Infof("status=success, confPath=%s, time=%d", confPath, utils.DiffMillis(now, time.Now()))
}

func NewEmptyEnv() []EnvVo {
	return []EnvVo{{Hostnames:[]HostnameVo{}, Name:""}}
}

func ResetConf() {
	if err := os.Remove(confPath); err != nil {
		logging.Errorf("reset=failed, err=%v", err)
		os.Exit(-1)
	}
	store.GetInstance().Clear()
}
