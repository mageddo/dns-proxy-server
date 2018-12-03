package timed

import (
	"github.com/mageddo/dns-proxy-server/cache"
	"time"
)

type TimedCache struct {

	// delegate cache
	cache cache.Cache

	// ttl in seconds
	ttl int64
}

func (c *TimedCache) Get(key interface{}) interface{} {
	if v, ok := c.cache.Get(key).(TimedValue); ok {
		if v.IsValid(time.Now()) {
			return v.Value()
		}
		c.Remove(key)
		return nil
	}
	return nil
}

func (c *TimedCache) GetTimeValue(key interface{}) interface{} {
	if v, ok := c.cache.Get(key).(TimedValue); ok {
		if v.IsValid(time.Now()) {
			return v
		}
		c.Remove(key)
		return nil
	}
	return nil
}

func (c *TimedCache) ContainsKey(key interface{}) bool {
	panic("don't use that! It will cause concurrency problems")
}

func (c *TimedCache) Put(key, value interface{}) {
	c.PutTTL(key, value, c.ttl)
}

func (c *TimedCache) PutTTL(key, value interface{}, ttl int64) {
	c.cache.Put(key, NewTimedValue(value, time.Now(), time.Duration(ttl) * time.Second))
}

func (c *TimedCache) PutIfAbsent(key, value interface{}) interface{} {
	return c.cache.PutIfAbsent(key, value)
}

func (c *TimedCache) Remove(key interface{}) {
	c.cache.Remove(key)
}

func (c *TimedCache) Clear() {
	c.cache.Clear()
}

func (c *TimedCache) KeySet() []interface{} {
	return c.cache.KeySet()
}

func (c *TimedCache) Size() int {
	return c.cache.Size()
}

func New(delegate cache.Cache, defaultTTL int64) *TimedCache {
	return &TimedCache{delegate, defaultTTL}
}
