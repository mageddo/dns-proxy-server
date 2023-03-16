---
title: Windows running instructions
weight: 1
---

## Running on Windows
You can run DPS on Windows host without any issues except by DPS isn't capable yet
to connect to docker API and solve containers ([see backlog issue][4]).

## Running as Docker Service

1- If you are using docker on your machine that's the best choice as it will automatically start DPS on every boot:

```bash
$ docker run -d --restart=unless-stopped -p 53:53/udp -p 53:53/tcp -p 5380:5380 defreitas/dns-proxy-server:3.11.0
```

2- Set DPS as the default DNS on Windows, check the instructions below on 
(_Configure DPS as default DNS on Windows_) or [click here][7]

3- Now you are able  to solve containers form local configuration, also network addresses keep solving as well

```bash
> nslookup dps-sample.dev
Server:  UnKnown
Address:  127.0.0.1

Non-authoritative answer:
Name:    dps-sample.dev
Addresses:  192.168.0.254
          192.168.0.254

> nslookup acme.com
Server:  UnKnown
Address:  127.0.0.1

Non-authoritative answer:
Name:    acme.com
Address:  23.93.76.124
```

You can access the GUI by the address http://localhost:5380/ , see [GUI Dashboard feature][8]

## Standalone Run
* Download the [latest release][5] for Windows
* Run dps by `dns-proxy-server.exe`

If you run DPS as administrator (using administrator prompt) then it will capable of configure itself as the [default DNS][6]
or check instructions below of how to set DPS as default DNS manually.

## Configure DPS as default DNS on Windows

**1. Find the host network ip v4 address**

You can also use `127.0.0.1` or if don't work your you try the following.
On prompt command run `ipconfig` and get one of the ipv4 addresses, it's recommended to get the IP from the real
network card, `192.168.0.128` in my case.

```bash
> ipconfig

Windows IP Configuration
Ethernet adapter Ethernet:

Connection-specific DNS Suffix  . : spo.virtua.com.br
IPv4 Address. . . . . . . . . . . : 192.168.0.128
Subnet Mask . . . . . . . . . . . : 255.255.255.0
Default Gateway . . . . . . . . . : 192.168.0.1
```

**2. Change your default internet adapter DNS to IP got on the last step**

* Press `Windows + R` and type `ncpa.cpl` then press **enter** or go to your network interfaces Window
* Change your default internet adapter DNS to ip you get the last step `127.0.0.1` or `192.168.0.128` in my case
by following the pictures below

![Screenshot](https://i.imgur.com/UAVUgLf.png?width=10pc&classes=shadow)

Uncheck IPV6 because Windows can try to solve hostnames by using a IPV6 DNS server,
then requests won't be sent to DPS, actually DPS doesn't support IPV6.

![Screenshot](https://i.imgur.com/DGPdFRD.png?width=10pc&classes=shadow)

![screenshot](https://i.imgur.com/EcZF6mG.png?width=10pc&classes=shadow)

![Screenshot](https://i.imgur.com/0bxASqd.png?width=10pc&classes=shadow)

[1]: https://imgur.com/a/LlDH8AM
[4]: https://github.com/mageddo/dns-proxy-server/issues/314
[5]: https://github.com/mageddo/dns-proxy-server/releases
[6]: {{%relref "2-features/auto-configuration-as-default-dns/_index.md" %}}
[7]: #configure-dps-as-default-dns-on-windows
[8]: {{%relref "2-features/gui/_index.md" %}}
