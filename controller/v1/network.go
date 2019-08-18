package v1

import (
	"context"
	"encoding/json"
	"github.com/mageddo/dns-proxy-server/controller/v1/vo"
	"github.com/mageddo/dns-proxy-server/docker/dockernetwork"
	. "github.com/mageddo/go-httpmap"
	"net/http"
)

const (
	NetworkDisconnect = "/network/disconnect-containers/"
)

func init() {

	Get(NetworkDisconnect, func(ctx context.Context, res http.ResponseWriter, req *http.Request) {
		res.Header().Add("Content-Type", "application/json")
		netId := req.URL.Query().Get("networkId")
		errs := dockernetwork.DisconnectNetworkContainers(ctx, netId)
		json.NewEncoder(res).Encode(vo.CreateNetworkDisconnectVO(errs))
	})

}
