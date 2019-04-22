package v1

import (
	"encoding/json"
	"fmt"
	"github.com/mageddo/dns-proxy-server/controller/v1/vo"
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
		if conf, err := local.LoadConfiguration(); conf != nil {
			envName := req.URL.Query().Get("env")
			if env, _ := conf.GetEnv(envName);  env != nil {
				json.NewEncoder(res).Encode(vo.FromEnv(env))
				return
			}
			BadRequest(res, fmt.Sprintf("Env %s not found", envName))
			return
		} else {
			confLoadError(res, err)
		}
	})

	Get(HOSTNAME_FIND, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, err := local.LoadConfiguration(); conf != nil {
			env := req.URL.Query().Get("env")
			hostname := req.URL.Query().Get("hostname")
			var err error
			var hostnames *[]localvo.Hostname
			if hostnames, err = conf.FindHostnameByNameAndEnv(ctx, env, hostname);  err == nil {
				json.NewEncoder(res).Encode(vo.FromHostnames(env, *hostnames))
				return
			}
			BadRequest(res, fmt.Sprintf(err.Error()))
			return
		} else {
			confLoadError(res, err)
		}
	})

	Post(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=create-hostname")
		var hostname vo.HostnameV1
		if err := json.NewDecoder(req.Body).Decode(&hostname); err != nil {
			res.Header().Add("Content-Type", "application/json")
			BadRequest(res, "Invalid JSON")
			return
		}
		logging.Infof("m=/hostname/, status=parsed-host, host=%+v", hostname)
		if err := local.AddHostname(hostname.Env, hostname.ToHostname()); err != nil {
			logging.Infof("m=/hostname/, status=error, action=create-hostname, hostname=%s", hostname.Hostname, err)
			BadRequest(res, err.Error())
		} else {
			res.WriteHeader(201)
			logging.Infof("m=/hostname/, status=success, action=create-hostname")
		}
	})

	Put(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=update-hostname")
		var hostname vo.HostnameV1
		if err := json.NewDecoder(req.Body).Decode(&hostname); err != nil {
			logging.Warningf("m=/hostname/, status=invalid-json, host=%+v", hostname, err)
			res.Header().Add("Content-Type", "application/json")
			BadRequest(res, "Invalid JSON")
			return
		}
		logging.Infof("m=/hostname/, status=parsed-host, host=%+v", hostname)
		if err := local.UpdateHostname(hostname.Env, hostname.ToHostname());  err != nil {
			logging.Infof("m=/hostname/, status=error, action=update-hostname, err=%+v", err)
			res.Header().Add("Content-Type", "application/json")
			BadRequest(res, err.Error())
		} else {
			logging.Infof("m=/hostname/, status=success, action=update-hostname")
		}
	})

	Delete(HOSTNAME, func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		logging.Infof("m=/hostname/, status=begin, action=delete-hostname")
		var hostname vo.HostnameV1
		json.NewDecoder(req.Body).Decode(&hostname)
		logging.Infof("m=/hostname/, status=parsed-host, action=delete-hostname, host=%+v", hostname)
			if err := local.RemoveHostnameByEnvAndHostname(hostname.Env, hostname.Hostname);  err != nil {
				logging.Infof("m=/hostname/, status=error, action=delete-hostname, err=%+v", err)
				res.Header().Add("Content-Type", "application/json")
				BadRequest(res, err.Error())
				return
		} else {
			logging.Infof("m=/hostname/, status=success, action=delete-hostname")
		}
	})
}
