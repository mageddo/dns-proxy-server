package vo

import "github.com/mageddo/dns-proxy-server/events/local/localvo"

type EnvV1 struct {
	Name string `json:"name"`
	Hostnames []HostnameV1 `json:"hostnames,omitempty"`
}

func (env *EnvV1) ToEnv() localvo.Env {
	return localvo.Env{
		Name:      env.Name,
		Hostnames: fromV1Hostnames(env.Hostnames),
	}
}

func FromEnvs(envs []localvo.Env) []EnvV1 {
	v1Envs := make([]EnvV1, len(envs))
	for i, env := range envs {
		v1Envs[i] = *FromEnv(&env)
	}
	return v1Envs
}

func FromEnv(env *localvo.Env) *EnvV1 {
	var envV1 EnvV1
	envV1.Name = env.Name
	envV1.Hostnames = FromHostnames(env.Hostnames)
	return &envV1
}
