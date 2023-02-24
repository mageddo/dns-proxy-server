---
title: Running it
weight: 1
---

### Running on Linux

#### On Docker

```bash
$ docker run --rm --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  defreitas/dns-proxy-server
```

If your system is periodically recreating `/etc/resolv.conf` (like `dhclient` does) and DPS stops working
after a while you may need to try the following variant instead (see
[issue 166](https://github.com/mageddo/dns-proxy-server/issues/166) for why this is):

```bash
$ docker run --rm --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc:/host/etc \
  -e MG_RESOLVCONF=/host/etc/resolv.conf \
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
* Change your default internet adapter DNS to `127.0.0.1` by following the 
pictures below

![Screenshot](https://i.imgur.com/UAVUgLf.png?width=10pc&classes=shadow)

Uncheck IPV6 because Windows can try to solve hostnames by using a IPV6 DNS server,
then requests won't be sent to DPS, actually DPS doesn't support IPV6.

![Screenshot](https://i.imgur.com/DGPdFRD.png?width=10pc&classes=shadow)

![screenshot](https://i.imgur.com/EcZF6mG.png?width=10pc&classes=shadow)

![Screenshot](https://i.imgur.com/0bxASqd.png?width=10pc&classes=shadow)

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

```bash
$ nslookup -port=8980 google.com 127.0.0.1
```

[1]: https://imgur.com/a/LlDH8AM
