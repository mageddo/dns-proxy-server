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
		envV1 := &v1Envs[i]
		envV1.Name = env.Name
		envV1.Hostnames = fromHostnames(env.Hostnames)
	}
	return v1Envs
}

