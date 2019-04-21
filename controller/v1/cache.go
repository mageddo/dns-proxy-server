package v1

import (
	"context"
	"net/http"
	"github.com/mageddo/dns-proxy-server/utils"
	"github.com/mageddo/dns-proxy-server/cache/store"
	"github.com/mageddo/go-logging"
	. "github.com/mageddo/go-httpmap"
)
const (
	CACHE_V1 = "/v1/caches"
	CACHE_SIZE_V1 = "/v1/caches/size"
)
func init() {

	Get(CACHE_V1, func(ctx context.Context, res http.ResponseWriter, req *http.Request) {

		c, encoder := store.GetInstance(), utils.GetJsonEncoder(res)
		logging.Debugf("m=%s, size=%d", CACHE_V1, c.Size())
		res.Header().Add("Content-Type", "application/json")

		cacheObject := make(map[string]interface{})
		for _, k := range c.KeySet() {
			cacheObject[k.(string)] = c.Get(k)
		}

		if err := encoder.Encode(cacheObject); err != nil {
			logging.Errorf("m=%s, err=%v", CACHE_V1, err)
			RespMessage(res, http.StatusServiceUnavailable, "Could not get caches, please try again later")
		}
	})

	Get(CACHE_SIZE_V1, func(ctx context.Context, res http.ResponseWriter, req *http.Request) {

		c, encoder := store.GetInstance(), utils.GetJsonEncoder(res)
		logging.Debugf("m=%s, size=%d", CACHE_SIZE_V1, c.Size())
		res.Header().Add("Content-Type", "application/json")

		if err := encoder.Encode(map[string]interface{}{"size": c.Size()}); err != nil {
			logging.Errorf("m=%s, err=%v", CACHE_SIZE_V1, err)
			RespMessage(res, http.StatusServiceUnavailable, "Temporary unavailable, please try again later")
		}
	})

}
