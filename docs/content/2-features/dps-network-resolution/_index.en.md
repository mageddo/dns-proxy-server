---
title: DPS docker network
weight: 9
---

At previous versions DPS had a caveat where you only would be able to access other docker containers, or access host, or 
be accessed by host if you put them all to a network, since 2.15.0 DPS can do this job for you.

It is a really helpful behavior when you are in development but maybe a security issue when you are in production this
way you can enable or disable this feature if you want. 

We can simulate the issue by the following example:

You have a container running on a overlay network, it means this container can not be accessed by the host or by 
others container which are not on it's network

docker-compose.yml
```yaml
version: '3'
services:
  nginx-1:
    image: nginx
    container_name: nginx-1
    hostname: nginx-1.app
    networks:
      - nginx-network

networks:
  nginx-network:
    driver: overlay
```

starting up the container and testing
```bash
$ docker-compose up
$ curl --connect-timeout 2 nginx-1.app
curl: (7) Failed to connect to nginx-1.app port 80: Connection timed out
```

So the solution for this is to specify a a bridge network on the docker-compose.yml and also have to specify 
that you wanna solve the ip of the bridge network instead of the overlay one

docker-compose.yml
```yaml
version: '3'
services:
  nginx-1:
    image: nginx
    container_name: nginx-1
    hostname: nginx-1.app
    networks:
      - nginx-network
      - nginx-network-bridge
    labels:
      dps.network: tmp_nginx-network-bridge

networks:
  nginx-network:
    driver: overlay
  nginx-network-bridge:
    driver: bridge
```

```bash
$ docker-compose up
$ curl -I --connect-timeout 2 nginx-3.app
HTTP/1.1 200 OK
```

So since 2.15.0 DPS can do all of this for you just by creating a bridge network and making sure all containers are 
connected to it, the dps container inclusively, this way you will not have issues to access a container from another, 
the host from a container or vice versa

You can enable this feature by 

__Activating by command line__

	./dns-proxy-server --dps-network-auto-connect

__Configuring at json config file__

```
...
"dpsNetworkAutoConnect": true
...
```

__Using environment variable__

```bash
MG_DPS_NETWORK_AUTO_CONNECT=1 ./dns-proxy-server
```

> OBS: even if this feature is disabled a fix was made and now DPS gives priority to solve bridge networks over
> others (if a bridge network were found)
