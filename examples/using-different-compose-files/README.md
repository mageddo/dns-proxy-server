When you are using different compose files DPS is not required as well, just create a common network for all services,
note you will not be able to solve containers from host

```bash
$ docker network create --attachable dps || true &&\
docker-compose -f docker-compose-nginx-server.yml up -d --force-recreate &&\
docker-compose -f docker-compose-client.yml up --force-recreate
```
