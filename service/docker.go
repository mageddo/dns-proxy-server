package service

import (
	"fmt"
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/dns-proxy-server/flags"
)

func NewDockerScript() *Script {

	script := `'PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin ; ` +
		`docker rm -f dns-proxy-server &> /dev/null ;` +
		`docker run -e HOSTNAMES=%s --name dns-proxy-server %s ` +
		`-v /opt/dns-proxy-server/conf:/app/conf ` +
		`-v /var/run/docker.sock:/var/run/docker.sock ` +
		`-v /etc/resolv.conf:/etc/resolv.conf ` +
		`defreitas/dns-proxy-server:%s'`
	script = fmt.Sprintf(
		script, fmt.Sprintf("dns.mageddo,%s", conf.FormatDpsDomain("dns")),
		getExposedPort(), flags.GetRawCurrentVersion(),
	)
	return &Script{script}
}

func getExposedPort() string {
	if flags.PublishServicePort()  {
		return fmt.Sprintf("-p %d:%d", conf.WebServerPort(), conf.WebServerPort())
	} else {
		return ""
	}
}
