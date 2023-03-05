---
title: Linux running instructions
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

> Actually, I recommend you to run DPS using standalone method when you want DPS to be automatically configured as the
> default DNS

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


[2]: {{%relref "2-features/dps-network-resolution/_index.md" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
