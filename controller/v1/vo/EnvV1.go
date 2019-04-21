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

func FromEnvs(envs []localvo.Env) []EnvV1 {
	v1Envs := make([]EnvV1, len(envs))
	for i, env := range envs {
		v1Envs[i].Name = env.Name
	}
	return v1Envs
}
