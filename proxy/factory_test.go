package proxy

import (
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
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

	assert.Nil(t, local.AddHostname( "", localvo.Hostname{
		Hostname: hostname, Type:localvo.CNAME, Ttl: 2, Target:"github.com",
	}))
	assert.Nil(t, local.AddHostname("", localvo.Hostname{
		Hostname: "github.com", Type: localvo.A, Ttl: 3, Ip: [4]byte{1, 2, 3, 4},
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
