package conf

import (
	"fmt"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/dns-proxy-server/utils/env"
	"github.com/mageddo/go-logging"
	"os"
	"strings"
)

func CpuProfile() string {
	return *flags.Cpuprofile
}

func Compress() bool {
	return *flags.Compress
}

func Tsig() string {
	return *flags.Tsig
}

func WebServerPort() int {
	if conf, _ := getConf(); conf != nil && conf.WebServerPort > 0 {
		return conf.WebServerPort
	}
	return *flags.WebServerPort
}

func DnsServerPort() int {
	if conf, _ := getConf(); conf != nil && conf.DnsServerPort > 0 {
		return conf.DnsServerPort
	}
	return *flags.DnsServerPort
}

func SetupResolvConf() bool {
	if conf, _ := getConf(); conf != nil && conf.DefaultDns != nil {
		return *conf.DefaultDns
	}
	return *flags.SetupResolvconf
}

func GetResolvConf() string {
	return GetString(os.Getenv(env.MG_RESOLVCONF), "/etc/resolv.conf")
}

func getConf() (*localvo.Configuration, error) {
	return local.LoadConfiguration()
}

func LogLevel() int {
	if lvl := os.Getenv(env.MG_LOG_LEVEL); lvl != "" {
		return logKeyToSyslogCode(lvl)
	}
	if conf, _ := getConf(); conf != nil && conf.LogLevel != "" {
		return logKeyToSyslogCode(conf.LogLevel)
	}
	return logKeyToSyslogCode(flags.LogLevel())
}

func logKeyToSyslogCode(key string) int {
	switch strings.ToUpper(key) {
	case "DEBUG":
		return logging.DEBUG
	case "INFO":
		return logging.INFO
	case "WARNING":
		return logging.WARNING
	case "ERROR":
		return logging.ERROR
	}
	panic("Unknown log level: " + key)
}


func LogFile() string {
	f := os.Getenv(env.MG_LOG_FILE)
	if conf, _ := getConf(); f == "" &&  conf != nil && conf.LogFile != "" {
		f = conf.LogFile
	}
	f = GetString(f, flags.LogToFile())

	if strings.ToLower(f) == "true" {
		return "/var/log/dns-proxy-server.log"
	}
	if strings.ToLower(f) == "false" {
		return ""
	}
	return f
}

func GetString(value, defaultValue string) string {
	if len(value) == 0 {
		return defaultValue
	}
	return value
}

func ShouldRegisterContainerNames() bool {
	if v := os.Getenv(env.MG_REGISTER_CONTAINER_NAMES); len(strings.TrimSpace(v)) > 0 {
		return v == "1"
	}
	if conf, _ := getConf(); conf != nil && conf.RegisterContainerNames != nil {
		return *conf.RegisterContainerNames
	}
	return flags.RegisterContainerNames()
}

func GetHostname() string {
	if hostname := os.Getenv(env.MG_HOST_MACHINE_HOSTNAME); len(strings.TrimSpace(hostname)) != 0 {
		return hostname
	}
	if conf, _ := getConf(); conf != nil &&  len(conf.HostMachineHostname) != 0 {
		return conf.HostMachineHostname
	}
	return *flags.HostMachineHostname
}

func FormatDpsDomain(subdomain string) string {
	return fmt.Sprintf("%s.%s", subdomain, GetDpsDomain())
}

func GetDpsDomain() string {
	if domain := os.Getenv(env.MG_DOMAIN); len(strings.TrimSpace(domain)) != 0 {
		return domain
	}
	if conf, _ := getConf(); conf != nil &&  len(conf.Domain) != 0 {
		return conf.Domain
	}
	return *flags.Domain
}

func DpsNetwork() bool {
	return DpsNetworkAutoConnect() || dpsNetwork0()
}

func dpsNetwork0() bool {
	if v := os.Getenv(env.MG_DPS_NETWORK); len(strings.TrimSpace(v)) > 0 {
		return v == "1"
	}
	if conf, _ := getConf(); conf.DpsNetwork != nil {
		return *conf.DpsNetwork
	}
	return flags.DpsNetwork()
}

func DpsNetworkAutoConnect() bool {
	if v := os.Getenv(env.MG_DPS_NETWORK_AUTO_CONNECT); len(strings.TrimSpace(v)) > 0 {
		return v == "1"
	}
	if conf, _ := getConf(); conf.DpsNetwork != nil {
		return *conf.DpsNetworkAutoConnect
	}
	return flags.DpsNetworkAutoConnect()
}

func GetResolvSetupSleepSecond() int {
    return *flags.ResolvSetupSleepSecond
}
