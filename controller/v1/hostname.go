package v1

import (
	"encoding/json"
	"fmt"
	"github.com/mageddo/dns-proxy-server/events/local"
	"github.com/mageddo/dns-proxy-server/events/local/localvo"
	. "github.com/mageddo/go-httpmap"
	"github.com/mageddo/go-logging"
	"golang.org/x/net/context"
	"net/http"
)

const (
	HOSTNAME = "/hostname/"
	HOSTNAME_FIND = "/hostname/find/"
)

func init(){

	Get(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, _ := local.LoadConfiguration(); conf != nil {
			envName := req.URL.Query().Get("env")
			if env, _ := conf.GetEnv(envName);  env != nil {
				json.NewEncoder(res).Encode(env)
				return
			}
			BadRequest(res, fmt.Sprintf("Env %s not found", envName))
			return
		}
		confLoadError(res)
	})

	Get(HOSTNAME_FIND, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, _ := local.LoadConfiguration(); conf != nil {
			env := req.URL.Query().Get("env")
			hostname := req.URL.Query().Get("hostname")
			var err error
			var hostnames *[]localvo.Hostname
			if hostnames, err = conf.FindHostnameByNameAndEnv(ctx, env, hostname);  err == nil {
				json.NewEncoder(res).Encode(hostnames)
				return
			}
			BadRequest(res, fmt.Sprintf(err.Error()))
			return
		}
		confLoadError(res)
	})

	Post(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=create-hostname")
		var hostname localvo.Hostname
		if err := json.NewDecoder(req.Body).Decode(&hostname); err != nil {
			res.Header().Add("Content-Type", "application/json")
			BadRequest(res, "Invalid JSON")
			return
		}
		logging.Infof("m=/hostname/, status=parsed-host, host=%+v", hostname)
		if conf, _ := local.LoadConfiguration(); conf != nil {
			if err := conf.AddHostname(hostname.Env, hostname); err != nil {
				logging.Infof("m=/hostname/, status=error, action=create-hostname, err=%+v", err)
				BadRequest(res, err.Error())
				return
			}
			res.WriteHeader(201)
			logging.Infof("m=/hostname/, status=success, action=create-hostname")
			return
		}
		confLoadError(res)
	})

	Put(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=update-hostname")
		var hostname localvo.Hostname
		if err := json.NewDecoder(req.Body).Decode(&hostname); err != nil {
			res.Header().Add("Content-Type", "application/json")
			BadRequest(res, "Invalid JSON")
			return
		}
		logging.Infof("m=/hostname/, status=parsed-host, host=%+v", hostname)
		if conf, _ := local.LoadConfiguration(); conf != nil {
			if err := conf.UpdateHostname(hostname.Env, hostname);  err != nil {
				logging.Infof("m=/hostname/, status=error, action=update-hostname, err=%+v", err)
				res.Header().Add("Content-Type", "application/json")
				BadRequest(res, err.Error())
				return
			}
			logging.Infof("m=/hostname/, status=success, action=update-hostname")
			return
		}
		confLoadError(res)
	})

	Delete(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=delete-hostname")
		var hostname localvo.Hostname
		json.NewDecoder(req.Body).Decode(&hostname)
		logging.Infof("m=/hostname/, status=parsed-host, action=delete-hostname, host=%+v", hostname)
		if conf, _ := local.LoadConfiguration(); conf != nil {
			if err := conf.RemoveHostnameByEnvAndHostname(hostname.Env, hostname.Hostname);  err != nil {
				logging.Infof("m=/hostname/, status=error, action=delete-hostname, err=%+v", err)
				res.Header().Add("Content-Type", "application/json")
				BadRequest(res, err.Error())
				return
			}
			logging.Infof("m=/hostname/, status=success, action=delete-hostname")
			return
		}
		confLoadError(res)
	})
}
