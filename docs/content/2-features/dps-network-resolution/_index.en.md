---
title: DPS docker network
weight: 9
---

At previous versions *DPS* had a caveat where you only would be able to access other docker containers, access host or 
be accessed if they were at a bridge network, DPS inclusively, this bridge network also had to be the first defined
on container networks to have sure *DPS* would solve to its IP, since **2.15.0** *DPS* can do this job for you.

It is a really helpful behavior when you are in development but maybe a security issue when you are in production, this
way you can enable or disable this feature if you want. 

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

> OBS: even if this feature is disabled a fix was made and now DPS gives priority to solve bridge networks over the
> others (if a bridge network were found for the container)

### Simulating the issue

We can simulate the issue by the following example:

You have a container running on a overlay network, it means the container can not be accessed by the host or by 
 containers which are not on its network

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

The solution for this is to specify a bridge network on the **docker-compose.yml** and also specify 
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
connected to it, this way you will not have issues to access a container from another, 
the host from a container or vice versa.
