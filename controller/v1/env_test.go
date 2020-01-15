package v1

import (
	"github.com/go-resty/resty"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/stretchr/testify/assert"
	"net/http/httptest"
	"testing"
)

func TestGetActiveEnvSuccess(t *testing.T) {
	// arrange

	local.ResetConf()
	s := httptest.NewServer(nil)
	defer s.Close()

	// act
	r, err := resty.R().Get(s.URL + ENV_ACTIVE)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(t, `{"name":""}`, r.String())

}

func TestPutChangeActiveEnvThatDoesNotExistsError(t *testing.T) {

	s := httptest.NewServer(nil)
	defer s.Close()

	r, err := resty.R().
		SetBody(`{"name": "testEnv"}`).
		Put(s.URL + ENV_ACTIVE)

	assert.Nil(t, err)
	assert.Equal(t, 400, r.StatusCode())
	assert.Equal(t, `{"code":400,"message":"Env not found: testEnv"}`, r.String())

}

func TestPutChangeActiveEnvSuccess(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{
		"remoteDnsServers": [], "envs": [{ "name": "testEnv" }]
	}`, utils.SolveRelativePath(*flags.ConfPath))
	assert.Nil(t, err)


	s := httptest.NewServer(nil)
	defer s.Close()

	// act

	r, err := resty.R().
		SetBody(`{"name": "testEnv"}`).
		Put(s.URL + ENV_ACTIVE)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().Get(s.URL + ENV_ACTIVE)
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(t, `{"name":"testEnv"}`, r.String())

}

func TestGetEnvsSuccess(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [{ "name": "SecondEnv" }]}`, utils.SolveRelativePath(*flags.ConfPath))
	assert.Nil(t, err)

	// act
	s := httptest.NewServer(nil)
	defer s.Close()
	r, err := resty.R().Get(s.URL + ENV)


	// assert
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(t, `[{"name":"SecondEnv"}]`, r.String())

}

func TestPostEnvSuccess(t *testing.T) {

	local.ResetConf()

	s := httptest.NewServer(nil)
	defer s.Close()

	r, err := resty.R().
		SetBody(`{
			"name": "ThirdEnv",
			"hostnames": [{"hostname": "github.com", "ip": [1,2,3,4], "ttl":30,"type":"A"}]
		}`).
		Post(s.URL + ENV)
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().Get(s.URL + ENV)
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(
		t,
		utils.Replace(
			`[{"name":""},{"name":"ThirdEnv","hostnames":[{"id":"$1","hostname":"github.com","ip":[1,2,3,4],"target":"","ttl":30,"type":"A","env":"ThirdEnv"}]}]`,
			r.String(),
			`"id":"(\d+)"`,
		),
		r.String(),
	)
}


func TestDeleteEnvSuccess(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [{ "name": "SecondEnv" }]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	r, err := resty.R().Get(s.URL + ENV)
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(t, `[{"name":"SecondEnv"}]`, r.String())

	// act
	r, err = resty.R().
		SetBody(`{"name": "SecondEnv"}`).
		Delete(s.URL + ENV)


	// assert

	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().Get(s.URL + ENV)
	assert.Nil(t, err)
	assert.Equal(t, 200, r.StatusCode())
	assert.Equal(t, `[{"name":""}]`, r.String())

}
