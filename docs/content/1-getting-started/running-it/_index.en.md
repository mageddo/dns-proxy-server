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

DPS actually is fully supported on MAC except by [docker limitation features][5]. Despite on this,
there is no additional configuration to run DPS on Mac, you can do just like [on Linux](#running-on-linux).

Download the [latest release][3], extract and run:
```bash
$ sudo ./dns-proxy-server
```

When running on standalone mode (not on docker container) DPS will be able to auto-configure itself as OSX default DNS, 
after 5 seconds you see something like `usingDnsConfigurator=DnsConfiguratorOSx` at the logs.

If by some reason it doesn't work or you want to configure it manually because are running DPS on a docker conainer,
then check the instructions bellow: 

To list available networks:
```bash
$ networksetup -listallnetworkservices
An asterisk (*) denotes that a network service is disabled.
USB 10/100/1000 LAN
Wi-Fi
Thunderbolt Bridge
```
In my case the right Network is `Wi-Fi`, before change anything let's check if it has some manual
configured DNS server:
```bash
$ networksetup -getdnsservers Wi-Fi
There aren't any DNS Servers set on Wi-Fi. 
```
If it returns some server IP then is a good idea to save it to restore the configurations later.

Let's set DPS as the default DNS Server, you can get DPS IP by search for `Starting UDP server` at the starting logs,
it's `192.168.0.14` in my case, remember you need to run DPS in port 53 as MAC doesn't accept custom port especification.

```bash
$ networksetup -setdnsservers Wi-Fi 192.168.0.14
```

If you need to remove the configured DNS server then it will use your network provider DNS
```bash
$ networksetup -setdnsservers Wi-Fi Empty
```

See [this thread][6] with more use cases.

[1]: https://imgur.com/a/LlDH8AM
[2]: {{%relref "2-features/dps-network-resolution/_index.md" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
[4]: https://github.com/mageddo/dns-proxy-server/issues/314
[5]: https://docs.docker.com/desktop/networking/#there-is-no-docker0-bridge-on-the-host
[6]: https://github.com/mageddo/dns-proxy-server/issues/44#issuecomment-1454379761
