---
title: Running it
weight: 1
---

### Running on Linux

#### Standalone run (Recommended)

Download the [latest release][3], extract and run:
```bash
$ sudo ./dns-proxy-server
```
Now DNS Proxy Server is your DNS server, to back everything to original state just press <kbd>CTRL</kbd> + <kbd>C</kbd>;

#### On Docker

> Actually I recomend you to run DPS using standalone method because an additional command on the host
will be necessary to set DPS as default DNS
when running DPS on docker if you're using **system-resolved**:

```bash
$ docker run --rm --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  --network host \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/systemd/:/host/etc/systemd \
  -v /etc/:/host/etc \
  defreitas/dns-proxy-server
```

If you're using **system-resolved** then run command below to restart systemd-resolved service
and make DPS as default DNS changes to take effect.

```bash
$ service systemd-resolved restart
```

Explaining: 

`--network host`: Running on host mode make it possible to DPS bind the 
DNS server port to the host network interface, this way all containers will have access to DPS address 
and use DPS features. 
If you don't want to use that option then you can consider use [DPS Network feature][2].

`/var/run/docker.sock`: Docker socket, so DPS can query the running containers and solve their IP when asked.

`/etc/systemd/:/host/etc/systemd` / `/etc/:/host/etc`: Depending on your distro you may are using system-resolved or 
vanila resolv.conf to configure available DNS Servers, DPS will look at both and choose the best to be configured.

### Running on Windows

You can run DPS on Windows host without any problems except by two features

* DPS won't be able to be set as default DNS automatically (instructions below)
* DPS isn't capable yet ([see backlog issue][4]) to connect to docker API and solve containers 

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

### Running on MAC

MAC isn't fully supported yet but as of DPS 3 we are one step closer to solve this issue, stay tight on
[the discussion][5] to keep up to date.


[1]: https://imgur.com/a/LlDH8AM
[2]: {{%relref "2-features/dps-network-resolution/_index.md" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
[4]: https://github.com/mageddo/dns-proxy-server/issues/314
[5]: https://github.com/mageddo/dns-proxy-server/issues/158
