package v1

import (
	"github.com/go-resty/resty"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/flags"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/stretchr/testify/assert"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGetHostnamesByEnv(t *testing.T) {

	defer local.ResetConf()
	local.LoadConfiguration()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [
		{ "name": "MyEnv", "hostnames": [{"hostname": "github.io", "ip": [1,2,3,4], "ttl": 55}] }
	]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	r, err := resty.R().
		SetQueryParam("env", "MyEnv").
		Get(s.URL + HOSTNAME)

	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())

	assert.Equal(
		t,
		utils.Replace(
			`{"name":"MyEnv","hostnames":[{"id":"$1","hostname":"github.io","ip":[1,2,3,4],"target":"","ttl":55,"type":"","env":"MyEnv"}]}`,
			r.String(), `id":"(\d+)"`,
		),
		r.String(),
	)

}

func TestGetHostnamesByEnvAndHostname(t *testing.T) {

	// arrange

	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [
		{ "name": "MyEnv", "hostnames": [{"hostname": "github.io", "ip": [1,2,3,4], "ttl": 55}] }
	]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	// act

	r, err := resty.R().
		SetQueryParam("env", "MyEnv").
		SetQueryParam("hostname", "github.io").
		Get(s.URL + HOSTNAME_FIND)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Equal(
		t,
		utils.Replace(
			`[{"id":"$1","hostname":"github.io","ip":[1,2,3,4],"target":"","ttl":55,"type":"","env":"MyEnv"}]`,
			r.String(), `"id":"(\d+)"`,
		),
		r.String(),
	)

}

func TestPostHostname(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [{ "name": "MyOtherEnv" }]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	// act

	r, err := resty.R().
		SetBody(`{"hostname": "github.io", "ip": [1,2,3,4], "ttl": 55, "env": "MyOtherEnv", "type": "A"}`).
		Post(s.URL + HOSTNAME)

	// assert

	assert.Nil(t, err)
	assert.Equal(t, http.StatusCreated, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().
		SetQueryParam("env", "MyOtherEnv").
		SetQueryParam("hostname", "github.io").
		Get(s.URL + HOSTNAME_FIND)

	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Equal(
		t,
		utils.Replace(
			`[{"id":"$1","hostname":"github.io","ip":[1,2,3,4],"target":"","ttl":55,"type":"A","env":"MyOtherEnv"}]`,
			r.String(), `"id":"(\d+)"`,
		),
		r.String(),
	)

}

func TestPostHostnameInvalidPayloadError(t *testing.T) {

	s := httptest.NewServer(nil)
	defer s.Close()

	r, err := resty.R().
		SetBody(`{`).
		Post(s.URL + HOSTNAME)

	assert.Nil(t, err)
	assert.Equal(t, http.StatusBadRequest, r.StatusCode())
	assert.Equal(t, `{"code":400,"message":"Invalid JSON: unexpected EOF"}`, r.String())

}

func TestPutHostname(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [
		{ "name": "MyEnv", "hostnames": [{"id": 999, "hostname": "github.io", "ip": [1,2,3,4], "ttl": 55}] }
	]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	// act
	r, err := resty.R().
		SetBody(`{"id": "999", "hostname": "github.io", "ip": [4,3,2,1], "ttl": 65, "env": "MyEnv"}`).
		Put(s.URL + HOSTNAME)

	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().
		SetQueryParam("env", "MyEnv").
		SetQueryParam("hostname", "github.io").
		Get(s.URL + HOSTNAME_FIND)

	// assert
	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Equal(t, `[{"id":"999","hostname":"github.io","ip":[4,3,2,1],"target":"","ttl":65,"type":"","env":"MyEnv"}]`, r.String())

}


func TestDeleteHostname(t *testing.T) {

	// arrange
	local.ResetConf()

	err := utils.WriteToFile(`{ "remoteDnsServers": [], "envs": [
		{ "name": "MyEnv", "hostnames": [{"hostname": "github.io", "ip": [1,2,3,4], "ttl": 55}] }
	]}`, utils.SolveRelativePath(*flags.ConfPath))

	s := httptest.NewServer(nil)
	defer s.Close()

	// act
	r, err := resty.R().
		SetBody(`{"hostname": "github.io", "env": "MyEnv"}`).
		Delete(s.URL + HOSTNAME)

	// assert

	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Empty(t, r.String())

	r, err = resty.R().
		SetQueryParam("env", "MyEnv").
		SetQueryParam("hostname", "github.io").
		Get(s.URL + HOSTNAME_FIND)

	assert.Nil(t, err)
	assert.Equal(t, http.StatusOK, r.StatusCode())
	assert.Equal(t, `[]`, r.String())

}
