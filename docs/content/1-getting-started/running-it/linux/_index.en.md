---
title: Linux running instructions
weight: 1
---

## Running on Linux

## Running as Docker Service

If you are using docker on your machine that's the best choice as it will automatically start DPS on every boot:

```bash
$ docker run --hostname dns.mageddo --restart=unless-stopped -d \
  --network host \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/systemd/:/host/etc/systemd \
  -v /etc/:/host/etc \
  defreitas/dns-proxy-server
```

It will configure DPS as default DNS on systemd-resolved or vanilla resolv.conf depending on your system. 
If you're using **system-resolved** then run command below to restart its service
and make DPS as default DNS changes to take effect.

```bash
$ service systemd-resolved restart
```

[Click here][1] to see usage samples

Explaining the running params:

`--network host`: Running on host mode make it possible to DPS bind the
DNS server port to the host network interface, this way all containers will have access to DPS address
and use DPS features.
If you don't want to use that option then you can consider use [DPS Network feature][2].

`/var/run/docker.sock`: Docker socket, so DPS can query the running containers and solve their IP when asked.

`/etc/systemd/:/host/etc/systemd` / `/etc/:/host/etc`: Depending on your distro you may are using system-resolved or
vanila resolv.conf to configure available DNS Servers, DPS will look at both and choose the best to be configured.


## Standalone run

Download the [latest release][3], extract and run:
```bash
$ sudo ./dns-proxy-server
```
Now DNS Proxy Server is your DNS server, to back everything to original state just press <kbd>CTRL</kbd> + <kbd>C</kbd>;

[Click here][1] to see usage samples


[1]: {{%relref "2-features/_index.md#main-features-use-cases" %}}
[2]: {{%relref "2-features/dps-network-resolution/_index.md" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
