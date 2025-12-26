---
title: Specify from which network solve container IP
weight: 2
---

If your container have multiple networks then you can specify which network to use when solving IP by specifying `dps.network` label.

Creating a container with two networks attached to
```bash
$ docker network create --attachable network1
$ docker network create --attachable network2
$ docker run --name nginx1 --rm --label dps.network=network1 --hostname server1.acme.com --network network1 nginx
$ docker network connect network2 nginx1
```

Getting networks masks
```bash
$ docker network inspect -f "{{ .IPAM.Config }}" network1
[{172.31.0.0/16  172.31.0.1 map[]}]

$ docker network inspect -f "{{ .IPAM.Config }}" network2
[{192.168.16.0/20  192.168.16.1 map[]}]
```

Solving container and checking that the solved IP will be respective to the configured network at `dps.network` label
```bash
$ nslookup server1.acme.com
Server:		172.17.0.3
Address:	172.17.0.3#53

Non-authoritative answer:
Name:	server1.acme.com
Address: 192.168.16.2
```

You can also specify default preferred networks

| Name                                               | Description                                                                             | Default Value   |
|----------------------------------------------------|-----------------------------------------------------------------------------------------|-----------------|
| `solver.docker.networks.preferred.names`           | Which networks DPS must prioritize when discovering container IP                        |                 |
| `solver.docker.networks.preferred.overrideDefault` | If will disable DPS and BRIDGE default networks when solving                            | false           |
