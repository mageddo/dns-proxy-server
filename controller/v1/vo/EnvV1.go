package vo

import "github.com/mageddo/dns-proxy-server/events/local/localvo"

type EnvV1 struct {
	Name string `json:"name"`
}

func (env *EnvV1) ToEnv() localvo.Env {
	return localvo.Env{
		Name: env.Name,
	}
}
