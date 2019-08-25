package store

import (
	"github.com/mageddo/dns-proxy-server/cache"
	"github.com/mageddo/dns-proxy-server/cache/lru"
)

var caches []cache.Cache
var mainCache cache.Cache
func init(){
	mainCache = lru.New(43690) // about 1 MB considering HostnameVo struct
}

//
// Singleton cache
//
func GetInstance() cache.Cache {
	return mainCache
}

func RegisterCache(c cache.Cache) cache.Cache {
	caches = append(caches, c)
	return c
}

func ClearAllCaches(){
	mainCache.Clear()
	for _, c := range caches {
		c.Clear()
	}
}
