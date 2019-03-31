package resolvconf

import (
	"bufio"
	"bytes"
	"errors"
	"fmt"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/go-logging"
	"io/ioutil"
	"net"
	"os"
	"os/exec"
	"strings"
	"syscall"
)

func RestoreResolvconfToDefault() error {
	logging.Infof("status=begin")
	hd := newDNSServerCleanerHandler()
	err := ProcessResolvconf(hd)
	logging.Infof("status=success, err=%v", err)
	return err
}

func SetMachineDNSServer(serverIP string) error {
	hd := newSetMachineDnsServerHandler(serverIP)
	return ProcessResolvconf(hd)
}

func GetSearchDomainEntry() (string, error) {
	fileRead, err := os.Open(conf.GetResolvConf())
	if err != nil {
		return "", err
	}
	defer fileRead.Close()
	scanner := bufio.NewScanner(fileRead)
	for ; scanner.Scan();  {
		line := scanner.Text()
		switch getDnsEntryType(line) {
		case SEARCH:
			return line[len(SEARCH) + 1:], nil
		}
	}
	return "", nil
}


func ProcessResolvconf( handler DnsHandler ) error {

	var newResolvConfBuff bytes.Buffer
	logging.Infof("status=begin")

	resolvconf := conf.GetResolvConf()
	fileRead, err := os.Open(resolvconf)
	if err != nil {
		return err
	}
	defer fileRead.Close()

	var (
		hasContent = false
		foundDnsProxyEntry = false
	)
	logging.Infof("status=open-conf-file, file=%s", fileRead.Name())
	scanner := bufio.NewScanner(fileRead)
	for scanner.Scan() {
		line := scanner.Text()
		hasContent = true
		entryType := getDnsEntryType(line)
		if entryType == PROXY {
			foundDnsProxyEntry = true
		}
		logging.Debugf("status=readline, line=%s, type=%s", line,  entryType)
		if r := handler.process(line, entryType); r != nil {
			newResolvConfBuff.WriteString(*r)
			newResolvConfBuff.WriteByte('\n')
		}
	}
	if r := handler.afterProcess(hasContent, foundDnsProxyEntry); r != nil {
		newResolvConfBuff.WriteString(*r)
		newResolvConfBuff.WriteByte('\n')
	}

	stats, _ := fileRead.Stat()
	length := newResolvConfBuff.Len()
	err = ioutil.WriteFile(resolvconf, newResolvConfBuff.Bytes(), stats.Mode())
	if err != nil {
		return err
	}
	logging.Infof("status=success, buffLength=%d", length)
	return nil
}

func getDNSLine(serverIP string) string {
	return "nameserver " + serverIP + " # dps-entry"
}

func getDnsEntryType(line string) DnsEntry {

	if strings.HasSuffix(line, "# dps-entry") {
		return PROXY
	} else if strings.HasPrefix(line, "# nameserver ") && strings.HasSuffix(line, "# dps-comment") {
		return COMMENTED_SERVER
	} else if strings.HasPrefix(line, "#") {
		return COMMENT
	} else if strings.HasPrefix(line, "nameserver") {
		return SERVER
	} else if strings.HasPrefix(line, "search") {
		return SEARCH
	} else {
		return ELSE
	}
}

func SetCurrentDNSServerToMachineAndLockIt() error {
	err := SetCurrentDNSServerToMachine()
	if err != nil {
		return err
	}
	return LockResolvConf()
}

func SetCurrentDNSServerToMachine() error {
	ip, err := GetCurrentIpAddress()
	logging.Infof("status=begin, ip=%s, err=%v", ip, err)
	if err != nil {
		return err
	}
	return SetMachineDNSServer(ip)
}

func LockResolvConf() error {
	return LockFile(true, conf.GetResolvConf())
}

func UnlockResolvConf() error {
	return LockFile(true, conf.GetResolvConf())
}

func LockFile(lock bool, file string) error {

	logging.Infof("status=begin, lock=%t, file=%s", lock, file)
	flag := "-i"
	if lock {
		flag = "+i"
	}
	cmd := exec.Command("chattr", flag, file)
	err := cmd.Run()
	if err != nil {
		logging.Warningf("status=error-at-execute, lock=%t, file=%s, err=%v", lock, file, err)
		return err
	}

	status := cmd.ProcessState.Sys().(syscall.WaitStatus)
	if status.ExitStatus() != 0 {
		logging.Warningf("status=bad-exit-code, lock=%t, file=%s", lock, file)
		return errors.New(fmt.Sprintf("Failed to lock file %d", status.ExitStatus()))
	}
	logging.Infof("status=success, lock=%t, file=%s", lock, file)
	return nil

}

type DnsHandler interface {
	process(line string, entryType DnsEntry) *string
	afterProcess(hasContent bool, foundDnsProxy bool) *string
}

type DnsEntry string

const(
	COMMENT DnsEntry = "COMMENT"
	COMMENTED_SERVER DnsEntry = "COMMENTED_SERVER"
	SERVER DnsEntry = "SERVER"
	PROXY DnsEntry = "PROXY"
	SEARCH DnsEntry = "SEARCH"
	ELSE DnsEntry = "ELSE"
)

func GetCurrentIpAddress() (string, error) {

	addrs, err := net.InterfaceAddrs()
	if err != nil {
		return "", err
	}
	for _, addr := range addrs {
		ip := addr.String()
		if strings.Contains(ip, "/") {
			if !strings.HasPrefix(ip, "127") {
				return ip[:strings.Index(ip, "/")], nil
			}
		}
	}
	return "", nil
}

const SEARCH_DOMAIN_KEY = "SEARCH_DOMAIN"
func GetSearchDomainEntryCached() (string, error){
	cache := store.GetInstance()
	if cache.ContainsKey(SEARCH_DOMAIN_KEY) {
		logging.Debugf("status=cached-search-domain, domain=%s", cache.Get(SEARCH_DOMAIN_KEY))
		return cache.Get(SEARCH_DOMAIN_KEY).(string), nil
	}
	logging.Debugf("status=hot-load-search-domain")
	entry, err := GetSearchDomainEntry()
	if err == nil {
		cache.Put(SEARCH_DOMAIN_KEY, entry)
	}
	return entry, err
}

func GetHostname(subdomain string) string {
	if domainEntry, err := GetSearchDomainEntryCached(); err == nil && len(domainEntry) !=0 {
		return fmt.Sprintf("%s.%s", subdomain, domainEntry)
	}
	return subdomain
}
