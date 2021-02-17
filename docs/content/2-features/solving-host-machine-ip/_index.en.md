---
title: Solve host machine IP from anywhere
weight: 6
---

Just use `host.docker`

```bash
$ ping host.docker
PING host.docker (172.21.0.1) 56(84) bytes of data.
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=1 ttl=64 time=0.086 ms
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=2 ttl=64 time=0.076 ms
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=3 ttl=64 time=0.081 ms
```

You can customize this hostname by setting 

Environment variable

```bash
$ docker run dns-proxy-server -e MG_HOST_MACHINE_HOSTNAME=$(cat /etc/hostname)
```

Command line option

```bash
$ ./dns-proxy-server --host-machine-hostname $(cat /etc/hostname)
```

Json configuration

```json
{
	"hostMachineHostname": "host.docker" 
}
```

**Notes**:

Be aware if you set the host machine hostname as the machine name then you will have to remove
its name from `/etc/hosts` since OS try to resolve names from hosts file first
then from DNS server 
