package v1

import (
	"net/http"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/go-logging"
)

func init(){

	http.HandleFunc("/static/", func(res http.ResponseWriter, req *http.Request){
		staticPath := utils.SolveRelativePath("/")
		logging.Infof("urlPath=%s", req.URL.Path)
		hd := http.FileServer(http.Dir(staticPath))
		hd.ServeHTTP(res, req)
	})
}
