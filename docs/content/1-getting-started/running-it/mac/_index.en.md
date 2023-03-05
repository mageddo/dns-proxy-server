---
title: MacOSX running instructions
weight: 1
---


### Running on MAC

DPS actually is fully supported on MAC except by [docker limitation features][5]. Despite on this,
there is no additional configuration to run DPS on Mac, you can do just like [on Linux](#running-on-linux).

Download the [latest release][3], extract and run:
```bash
$ sudo ./dns-proxy-server
```

When running on standalone mode (not on docker container) DPS will be able to auto-configure itself as OSX default DNS,
after 5 seconds you see something like `usingDnsConfigurator=DnsConfiguratorOSx` at the logs.

### Configuring DPS as default DNS manually

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

[3]: https://github.com/mageddo/dns-proxy-server/releases
[5]: https://docs.docker.com/desktop/networking/#there-is-no-docker0-bridge-on-the-host
[6]: https://github.com/mageddo/dns-proxy-server/issues/44#issuecomment-1454379761
