---
title: Access container by it's container name / service name
weight: 7
---

```bash
$ docker run --rm --name my-nginx nginx
```

```bash
$ nslookup my-nginx.docker
Server:		172.17.0.3
Address:	172.17.0.3#53

Non-authoritative answer:
Name:	my-nginx.docker
Address: 10.0.2.3
```

You can enable this feature by 

__Activating by command line__

	./dns-proxy-server -register-container-names

__Configuring at json config file__

```
...
"registerContainerNames": true
...
```

__Using environment variable__

```bash
MG_REGISTER_CONTAINER_NAMES=1 ./dns-proxy-server
```

You can also  customize the domain from docker to whatever you want by
 
__Activating by command line__

	./dns-proxy-server --domain docker

__Configuring at json config file__

```
...
"domain": "docker"
...
```

__Using environment variable__

```bash
MG_DOMAIN=docker ./dns-proxy-server
```
