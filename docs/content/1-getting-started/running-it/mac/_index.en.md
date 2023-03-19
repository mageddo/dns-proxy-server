---
title: MacOSX running instructions
weight: 1
---
## Running on MAC

DPS actually is fully supported on MAC knowing [docker limitation features][5]
which can be bypassed with a [reverse-proxy][10].

## Running as Docker as service

Running DPS as docker service
```bash
$ docker run -d -p 5380:5380 -p 53:53/udp -p 53:53/tcp --restart=unless-stopped \
  -v /var/run/docker.sock:/var/run/docker.sock \
  defreitas/dns-proxy-server
```

* Go to [configuring DPS as default DNS][7] to complete DPS configuration.
* [Check this][1] for usage samples.

## Standalone run

Download the [latest release][3] (I recomend the jar one), extract and run:
```bash
$ sudo java -jar ./dns-proxy-server
```

[Check this][1] for usage samples.

When running on standalone mode (not on docker container) DPS will be able to auto configure itself as OSX default DNS,
after 5 seconds you see something like `usingDnsConfigurator=DnsConfiguratorOSx` at the logs.

## Configuring DPS as default DNS manually

When not running in standalone mode you will need to configure DPS as default DPS manually,
check the instructions below:

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
If it returns some server IP then is a good idea to backup it then you can restore the configurations later.

Let's set DPS as the default DNS Server, best option here is to get your network IP address, `Wi-Fi` in my case which
has `192.168.0.14` set, `127.0.0.1` will partially works because this way containers won't be able 
to solve each other, just the host solve the containers.

**Obs**: Be aware you need to run DPS in port **53** as MAC doesn't accept custom port especification.

```bash
$ networksetup -setdnsservers Wi-Fi 192.168.0.14
```

If you need to remove the configured DNS server then it will use your network provider DNS
```bash
$ networksetup -setdnsservers Wi-Fi Empty
```

See [this thread][6] with more use cases.

[1]: {{%relref "2-features/_index.md#main-features-use-cases" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
[5]: https://docs.docker.com/desktop/networking/#there-is-no-docker0-bridge-on-the-host
[6]: https://github.com/mageddo/dns-proxy-server/issues/44#issuecomment-1454379761
[7]: #configuring-dps-as-default-dns-manually
[10]: {{%relref "5-tutorials/docker-reverse-proxy/_index.md" %}}
