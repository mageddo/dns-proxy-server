package proxy

import (
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/miekg/dns"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestShouldSolveCnameIp(t *testing.T){

	// arrange

	hostname := "mageddo.github.com"

	solverFactory := NewCnameDnsSolverFactory(&DefaultDnsSolverFactory{})

	question := new(dns.Question)
	question.Name = hostname + "."

	defer local.ResetConf()
	conf, err := local.LoadConfiguration()
	assert.Nil(t, err, "failed to load configuration")

	assert.Nil(t, conf.AddHostname( "", local.HostnameVo{
		Hostname: hostname, Type:local.CNAME, Env: "", Ttl: 2, Target:"github.com",
	}))
	assert.Nil(t, conf.AddHostname( "", local.HostnameVo{
		Hostname: "github.com", Type:local.A, Env: "", Ttl: 3, Ip:[4]byte{1,2,3,4},
	}))

	// act
	msg, err := solverFactory.Solve(ctx, *question, []DnsSolver{
		&localDnsSolver{},
	})

	// assert
	assert.Nil(t, err)
	assert.Equal(t, 2, len(msg.Answer))
	assert.Equal(t, "mageddo.github.com.\t2\tIN\tCNAME\tgithub.com.", msg.Answer[0].String())
	assert.Equal(t, "github.com.\t3\tIN\tA\t1.2.3.4", msg.Answer[1].String())
}
