---
title: Running it
weight: 1
---

### Running on Linux

#### On docker

```bash
$ docker run --rm --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  defreitas/dns-proxy-server
```

#### Standalone run

Download the [latest version](https://github.com/mageddo/dns-proxy-server/releases), extract and run

	$ sudo ./dns-proxy-server

Dns Proxy Server is now your current DNS server, to back everything to original state just press `CTRL + C`

### Running on Windows

> We have [cases](https://github.com/mageddo/dns-proxy-server/issues/66) of people got DPS running on Windows,
below the information we have of how to run DPS on these OS, if you confirm that or have some information that
would be helpful to archieve this please contribute creating a pull request or issue documenting that

1. Start up DPS
```bash
docker run --name dns-proxy-server -p 5380:5380 -p 53:53/udp \
  -v /var/run/docker.sock:/var/run/docker.sock \ 
  defreitas/dns-proxy-server
```

2. Change your default internet adapter DNS to `127.0.0.1`

* Press `Windows + R` and type `ncpa.cpl` then press **enter** or go to your network interfaces Window
* Change your default internet adapterDNS to `127.0.0.1` by following the below 
pictures (sorry they are in portuguese)

![Screenshot](https://i.imgur.com/1goUHp0.png?width=10pc&classes=shadow)
![Screenshot](https://i.imgur.com/XKM7JwN.png?width=10pc&classes=shadow)
![Screenshot](https://i.imgur.com/EFno6F6.png?width=10pc&classes=shadow)

### Testing the DNS server

Starting some docker container and keeping it alive for DNS queries

```bash
$ docker run --rm --hostname nginx.dev.intranet \
  -e 'HOSTNAMES=nginx2.dev.intranet,nginx3.dev.intranet' nginx
```

Solving the docker container hostname from Dns Proxy Server

```bash
$ nslookup nginx.dev.intranet
Server:		172.22.0.6
Address:	172.22.0.6#53

Non-authoritative answer:
Name:	debian.dev.intranet
Address: 172.22.0.7
```

Google keep working was well

```bash
$ nslookup google.com
Server:		172.22.0.6
Address:	172.22.0.6#53

Non-authoritative answer:
Name:	google.com
Address: 172.217.29.206
```

Start the server at [custom port](#configure-your-dns) and solving from it

	nslookup -port=8980 google.com 127.0.0.1
