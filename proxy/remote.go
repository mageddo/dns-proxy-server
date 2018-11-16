package proxy

import (
	"github.com/miekg/dns"
	"net"
	"errors"
	"fmt"
	"golang.org/x/net/context"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/go-logging"

	"github.com/mageddo/dns-proxy-server/cache/store"
)

const SERVERS = "SERVERS"

type remoteDnsSolver struct {
	confloader func(ctx context.Context) (*local.LocalConfiguration, error)
}

// reference https://miek.nl/2014/August/16/go-dns-package/
func (r remoteDnsSolver) Solve(ctx context.Context, question dns.Question) (*dns.Msg, error) {
	c := store.GetInstance()
	client := new(dns.Client)
	m := new(dns.Msg)
	m.SetQuestion(dns.Fqdn(question.Name), question.Qtype) // CAN BE A, AAA, MX, etc.
	m.RecursionDesired = true

	var config *local.LocalConfiguration
	var err error
	if !c.ContainsKey(SERVERS) {
		logging.Debugf("solver=remote, status=servers-hot-load")
		if config, err = r.confloader(ctx); err != nil {
			logging.Errorf("error=%v",err)
			return nil, err
		}
		c.PutIfAbsent(SERVERS, config)
	} else {
		logging.Debugf("solver=remote, status=servers-from-cache")
	}
	config = c.Get(SERVERS).(*local.LocalConfiguration)

	var res *dns.Msg
	for _, server := range config.GetRemoteServers(ctx) {

		if len(server) != 4 {
			logging.Warning("status=wrong-server, server=%+v", server)
			continue
		}

		// server and port to ask
		formatServer := fmt.Sprintf("%d.%d.%d.%d", server[0], server[1], server[2], server[3])
		logging.Debugf("status=format-server, server=%s", formatServer)

		res, _, err = client.Exchange(m, net.JoinHostPort(formatServer, "53"))

		// if the answer not be returned
		if res == nil {
			err = errors.New(fmt.Sprintf("status=answer-can-not-be-null, err=%v", err))
			logging.Infof("status=no-answer, question=%s, server=%s, err=%s", question.Name, formatServer, err)
			continue
		} else if res.Rcode != dns.RcodeSuccess { // what the code of the return message ?
			err = errors.New(fmt.Sprintf("status=invalid-answer-name, name=%s, rcode=%d", question.Name, res.Rcode))
			logging.Infof("status=bad-code, name=%s, rcode=%d, err=%s", question.Name, res.Rcode, err)
			continue
		}
		return res, nil
	}
	logging.Infof("status=complete, name=%s, res=%d, err=%s", question.Name, getRCode(res), err)

	return res, err
}

func NewRemoteDnsSolver() *remoteDnsSolver {
	return &remoteDnsSolver{
		confloader: func(ctx context.Context) (*local.LocalConfiguration, error) {
			return local.LoadConfiguration()
		}}
}


func getRCode(msg *dns.Msg) int {
	if msg == nil {
		return -1
	}
	return msg.Rcode
}
