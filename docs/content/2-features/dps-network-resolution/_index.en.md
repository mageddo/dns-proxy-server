---
title: DPS docker network
weight: 9
---
Since: 2.15.0

DPS can create its own network which is called `dps` and connect all running containers to that network this way
all containers can comunicate to each other, when container A solve container B IP, which will be able to
ping B cause they are on the same network (dps). Observes that you are also able to create your own network and
make this job of connect the containers you want to be able to talk each other.

__Activating by command line__

```bash
./dns-proxy-server --dps-network-auto-connect
```

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

> OBS: with this feature disabled or not, DPS gives priority to solve bridge networks over the
> others (if a bridge network were found for the container)

### Why it is necessary

We can simulate the issue by the following example:

You have a container running on an overlay network (so you need to be running docker in a swarm mode),
then it means the container can not be accessed by the host or by containers which are not in this same network

docker-compose.yml
```yaml
version: '3.2'
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
    attachable: true
```

starting up the container and testing
```bash
$ docker-compose up
$ curl --connect-timeout 2 nginx-1.app
curl: (7) Failed to connect to nginx-1.app port 80: Connection timed out
```

The solution for it can be use `dps` network or specify a bridge network on the **docker-compose.yml** 

docker-compose.yml
```yaml
version: '3.2'
services:
  nginx-1:
    image: nginx
    container_name: nginx-1
    hostname: nginx-1.app
    networks:
      - nginx-network
      - nginx-network-bridge

networks:
  nginx-network:
    driver: overlay
    attachable: true
  nginx-network-bridge:
    driver: bridge
```

```bash
$ docker-compose down
$ docker-compose up
$ curl -I --connect-timeout 2 nginx-1.app
HTTP/1.1 200 OK
```
