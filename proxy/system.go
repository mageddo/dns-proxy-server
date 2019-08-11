package proxy

import (
	"errors"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/docker/dockernetwork"
	"github.com/mageddo/dns-proxy-server/resolvconf"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/go-logging"
	"github.com/miekg/dns"
	"golang.org/x/net/context"
	"net"
	"reflect"
	"regexp"
	"strconv"
	"strings"
)

type SystemDnsSolver struct {}

func (s SystemDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	questionName := question.Name[:len(question.Name)-1]
	switch questionName {
	case conf.GetHostname(), resolvconf.GetHostname(conf.GetHostname()):
		if ip, err := dockernetwork.FindNetworkGatewayIp(ctx, dockernetwork.DpsNetwork); err != nil {
			ip, err = getLocalMachineIp(ctx, questionName)
		} else {
			logging.Infof("status=solved, solver=system, question=%s", ctx, questionName, err)
			return s.getMsg(questionName, ip, question), err
		}
	}
	return nil, errors.New("host not found")
}

func getLocalMachineIp(ctx context.Context, questionName string) (string, error) {
	ip, err, code := utils.Exec("sh", "-c", "ip r | awk '/default/{print $3}'")
	if code == 0 {
		clearedIP := regexp.MustCompile(`\s`).ReplaceAllLiteralString(string(ip), ``)
		logging.Infof("status=solved, solver=system, question=%s, ip=%s", ctx, questionName, clearedIP)
		return clearedIP, nil
	}
	return "", err
}

func (s SystemDnsSolver) Name() string {
	return reflect.TypeOf(s).String()
}

func NewSystemSolver() SystemDnsSolver {
	return SystemDnsSolver{}
}

func (s SystemDnsSolver) getMsg(key, ip string, question dns.Question) *dns.Msg {
	ipArr := strings.Split(ip, ".")
	i1, _ := strconv.Atoi(ipArr[0])
	i2, _ := strconv.Atoi(ipArr[1])
	i3, _ := strconv.Atoi(ipArr[2])
	i4, _ := strconv.Atoi(ipArr[3])

	rr := &dns.A{
		Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 0},
		A:   net.IPv4(byte(i1), byte(i2), byte(i3), byte(i4)),
	}

	m := new(dns.Msg)
	m.Answer = append(m.Answer, rr)
	return m
}
