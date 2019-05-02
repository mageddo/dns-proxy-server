---
title: Installing DPS as service
weight: 2
---

### Installing as docker service

```bash
docker run --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  --restart=unless-stopped -d \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  defreitas/dns-proxy-server
```

### Installing as linux service

1. Download the [latest release](https://github.com/mageddo/dns-proxy-server/releases) and extract it
2. Run the service installer

```
$ sudo ./dns-proxy-server -service=docker
```
3. Then follow the progress at the log file
```
$ tail -f /var/log/dns-proxy-server.log 
```

this way it will start with the **OS**

if you want to stop 

	$ sudo service dns-proxy-server stop
	Stopping service…
	Service stopped

if you don't want this service anymore

	$ sudo service dns-proxy-server uninstall
	Are you really sure you want to uninstall this service? That cannot be undone. [yes|No] 
	yes
