When you are using different compose files DPS is not required as well, just create a common network for all services,
note you will not be able to solve containers from host

```bash
$ docker network create --attachable dps || true &&\
docker-compose -f docker-compose-nginx-server.yml up -d --force-recreate &&\
docker-compose -f docker-compose-client.yml up --force-recreate
```

Docker version

```
docker version
Client: Docker Engine - Community
 Version:           18.09.0
 API version:       1.39
 Go version:        go1.10.4
 Git commit:        4d60db4
 Built:             Wed Nov  7 00:46:51 2018
 OS/Arch:           linux/amd64
 Experimental:      false

Server: Docker Engine - Community
 Engine:
  Version:          18.09.0
  API version:      1.39 (minimum version 1.12)
  Go version:       go1.10.4
  Git commit:       4d60db4
  Built:            Wed Nov  7 00:52:55 2018
  OS/Arch:          linux/amd64
  Experimental:     false

```
