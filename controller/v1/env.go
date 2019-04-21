package v1

import (
	"github.com/mageddo/dns-proxy-server/controller/v1/vo"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	"net/http"
	"github.com/mageddo/dns-proxy-server/events/local"
	"encoding/json"
	"context"
	"github.com/mageddo/dns-proxy-server/utils"
	. "github.com/mageddo/go-httpmap"
	"github.com/mageddo/go-logging"
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
		var envVo localvo.Env
		json.NewDecoder(req.Body).Decode(&envVo)
		logging.Infof("m=/env/, status=parsed-host, env=%+v", envVo)
		if conf, _ := local.LoadConfiguration(); conf != nil {
			if err := conf.AddEnv(ctx, envVo);  err != nil {
				logging.Infof("m=/env/, status=error, action=create-env, err=%+v", err)
				BadRequest(res, err.Error())
				return
			}
			logging.Infof("m=/env/, status=success, action=create-env")
			return
		}
		confLoadError(res)
	})

	Delete(ENV, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		logging.Infof("m=/env/, status=begin, action=delete-env")
		var env localvo.Env
		json.NewDecoder(req.Body).Decode(&env)
		logging.Infof("m=/env/, status=parsed-host, action=delete-env, env=%+v", env)
		if conf, _ := local.LoadConfiguration(); conf != nil {
			if err := conf.RemoveEnvByName(env.Name);  err != nil {
				logging.Infof("m=/env/, status=error, action=delete-env, err=%+v", err)
				BadRequest(res, err.Error())
				return
			}
			logging.Infof("m=/env/, status=success, action=delete-env")
			return
		}
		confLoadError(res)
	})
}

func confLoadError(res http.ResponseWriter){
	BadRequest(res, "Could not load conf")
}
