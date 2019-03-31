package conf

import (
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/flags"
	"os"
	"github.com/mageddo/dns-proxy-server/utils/env"
	"strings"
	"github.com/mageddo/go-logging"
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

func getConf() (*local.LocalConfiguration, error) {
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

func RegisterContainerNames() bool {
	if v := os.Getenv(env.MG_REGISTER_CONTAINER_NAMES); v == "1" {
		return true
	}
	if conf, _ := getConf(); conf != nil && conf.RegisterContainerNames != nil {
		return *conf.RegisterContainerNames
	}
	return flags.RegisterContainerNames()
}

func GetHostname() string {
	if hostname := os.Getenv(env.MG_HOSTNAME); len(strings.Trim(hostname, " ")) != 0 {
		return hostname
	}
	if conf, _ := getConf(); conf != nil &&  len(conf.Hostname) != 0 {
		return conf.Hostname
	}
	return *flags.Hostname
}
