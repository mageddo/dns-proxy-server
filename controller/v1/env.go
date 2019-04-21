package v1

import (
	"context"
	"encoding/json"
	"github.com/mageddo/dns-proxy-server/controller/v1/vo"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/pkg/mageddo/uuid"
	"github.com/mageddo/dns-proxy-server/reference"
	"github.com/mageddo/dns-proxy-server/utils"
	. "github.com/mageddo/go-httpmap"
	"github.com/mageddo/go-logging"
	"net/http"
	"time"
)

const (
	ENV = "/env/"
	// reference to the active environment
	ENV_ACTIVE = "/env/active"
)

func init(){

	Get(ENV_ACTIVE, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, _ := local.LoadConfiguration(); conf != nil {
			utils.GetJsonEncoder(res).Encode(vo.EnvV1{Name: conf.ActiveEnv})
			return
		}
		confLoadError(res)
	})

	Put(ENV_ACTIVE, func(ctx context.Context, res http.ResponseWriter, req *http.Request){

		logging.Infof("m=/env/active/, status=begin")
		res.Header().Add("Content-Type", "application/json")

		var envVo vo.EnvV1
		json.NewDecoder(req.Body).Decode(&envVo)
		logging.Infof("m=/env/active/, status=parsed-host, env=%+v", envVo)
		if err := local.SetActiveEnv(envVo.ToEnv()); err == nil {
			logging.Infof("m=/env/active/, status=success, action=active-env")
		} else {
			logging.Infof("m=/env/active, status=error, action=create-env, err=%+v", err)
			BadRequest(res, err.Error())
		}
	})

	Get(ENV, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, _ := local.LoadConfiguration(); conf != nil {
			utils.GetJsonEncoder(res).Encode(vo.FromEnvs(conf.Envs))
			return
		}
		confLoadError(res)
	})

	Post(ENV, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		logging.Infof("m=/env/, status=begin, action=create-env")
		var envVo vo.EnvV1
		json.NewDecoder(req.Body).Decode(&envVo)
		logging.Infof("m=/env/, status=parsed-host, env=%+v", envVo)
		for i := range envVo.Hostnames {
			envVo.Hostnames[i].Id = time.Now().UnixNano()
		}
		if err := local.AddEnv(ctx, envVo.ToEnv()); err == nil {
			logging.Infof("m=/env/, status=success, action=create-env")
			return
		} else {
			logging.Infof("m=/env/, status=error, action=create-env, err=%+v", err)
			BadRequest(res, err.Error())
		}
	})

	Delete(ENV, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		logging.Infof("m=/env/, status=begin, action=delete-env")
		var env vo.EnvV1
		json.NewDecoder(req.Body).Decode(&env)
		logging.Infof("m=/env/, status=parsed-host, action=delete-env, env=%+v", env)
		if err := local.RemoveEnvByName(context.WithValue(ctx, reference.UUID, uuid.UUID()), env.Name);  err != nil {
				logging.Infof("m=/env/, status=error, action=delete-env, err=%+v", err)
				BadRequest(res, err.Error())
		} else {
			logging.Infof("m=/env/, status=success, action=delete-env")
		}
	})
}

func confLoadError(res http.ResponseWriter){
	BadRequest(res, "Could not load conf")
}
