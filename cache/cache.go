package cache

type Cache interface {

	Get(key interface{}) interface{}

	// Deprecated: don't use that! It will lead to concurrency problems
	ContainsKey(key interface{}) bool
	Put(key, value interface{})

	// put only if don't exists else return actual value
	PutIfAbsent(key, value interface{}) interface{}
	Remove(key interface{})
	Clear()
	KeySet() []interface{}
	Size() int

}
