package v1

import (
	"net/http"
	"golang.org/x/net/context"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/dns-proxy-server/events/local"
	. "github.com/mageddo/go-httpmap"
)

func init(){
	Get("/", func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Location", "/static")
		res.WriteHeader(301)
	})

	Get("/configuration/", func(ctx context.Context, res http.ResponseWriter, req *http.Request){
		res.Header().Add("Content-Type", "application/json")
		if conf, _ := local.LoadConfiguration(); conf != nil {
			utils.GetJsonEncoder(res).Encode(conf)
			return
		}
		confLoadError(res)
	})
}
