---
title: Disable specific containers from solve
---

If you want to prevent a specific container from be resolved from Docker Solver then
you can set docker label `dps.container.enabled=false`.

Example

```bash
docker run --rm --name nginx-disabled -l "dps.container.enabled=false" nginx
```

Or docker-compose.yml

```yaml
services:
  nginx-disabled:
    image: nginx
    container_name: nginx-disabled
    labels:
      dps.container.enabled: "false"
```

```bash
$ nslookup nginx-disabled.docker -po=5354 127.0.0.1
Server:		127.0.0.1
Address:	127.0.0.1#5354

** server can't find nginx-disabled.docker: NXDOMAIN
```
