---
title: Solving Docker Containers from Hostname
weight: 4
---

DPS can assign a hostname to your docker containers to solve it's IPs by the :

* `HOSTNAMES` env
* Container Hostname
* Container Name
* Docker Compose Service Name

These are indivual forms of set a hostname to a container to query it, so you only need to use one of them.

## Hostnames Env

Creating a test container 
```bash
$ docker run -e HOSTNAMES="nginx1.dev,nginx1.docker" nginx
```

Solving from `HOSTNAMES` env

```bash
$ dig nginx1.dev @127.0.0.1 +noall +answer
nginx1.dev.		30	IN	A	172.17.0.2

$ dig nginx1.docker @127.0.0.1 +noall +answer
nginx1.docker.		30	IN	A	172.17.0.2
```

## Container Hostname

DPS will register the `${Container Hostname} + '.' ${Domain Name}` when Domain Name is set, if not set
only the Hostname will be used.

Creating a test container (way 1)
```bash
$ docker run --rm  --hostname nginx1 --domainname app nginx
```

Creating a test container (way 2)
```bash
$ docker run --rm  --hostname nginx1.app nginx
```

Testing hostname
```bash
$ dig nginx1.app @127.0.0.1 +noall +answer
nginx1.app.		30	IN	A	172.17.0.2
```

### Container Name

You can solve by the container name, this feature is disabled by default,
so you need to enable it using `MG_REGISTER_CONTAINER_NAMES=1` env, 
see a [complete tutorial about this feature][4] for more details.

Creating a test container 

```bash
$ docker run --rm --name nginx1 nginx
```

Testing 
```bash
$ dig nginx1.docker @127.0.0.1 +noall +answer
nginx1.docker.		30	IN	A	172.17.0.2
```

You can customize the `.docker` domain, with the `MG_DOMAIN` env, [see the docs][2] 
for more details.


### Docker Compose Service Name

Works like [Container Name][3] feature, but in this case you can solve by the service name
used at the container docker-compose.yml file.

docker-compose.yml
```yaml
services:
  nginx:
    image: nginx
```

```bash
$ dig nginx.docker @127.0.0.1 +noall +answer
nginx.docker.		30	IN	A	172.23.0.3
```

**Important** 

Be aware, when using docker-compose, specially with different docker-compose files you may have to lead with different 
[docker networks limitations][5].

[1]: {{%relref "3-configuration/_index.md" %}}#register-container-names
[2]: {{%relref "3-configuration/_index.md" %}}#domain
[3]: #container-name
[4]: {{%relref "2-features/accessing-container-by-name/_index.md" %}}
[5]: {{%relref "2-features/docker-different-networks-solving/_index.md" %}}
