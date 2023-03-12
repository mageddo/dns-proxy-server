---
title: Windows running instructions
weight: 1
---

## Running on Windows
You can run DPS on Windows host without any issues except by DPS isn't capable yet ([see backlog issue][4])
to connect to docker API and solve containers.

### Standalone Run
* Download the [latest release][5], currently only the jar binary is available for DPS, JRE 19+ is required.
* Run dps by `java -jar dns-proxy-server.jar`

If you run DPS as administrator (using administrator prompt) then it will capable of configure itself as the [default DNS][6]
or check instructions below of how to set DPS as default DNS manually.

### Running on Docker
DPS on Docker have one limitation, it won't be able to set itself as default DNS automatically (instructions below)

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

#### Testing the DNS server

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
[4]: https://github.com/mageddo/dns-proxy-server/issues/314
[5]: https://github.com/mageddo/dns-proxy-server/releases
[6]: {{%relref "2-features/auto-configuration-as-default-dns/_index.md" %}}
