---
title: Linux running instructions
weight: 1
---

## Running on Linux

## Standalone run

Download the [latest release][3], extract and run:
```bash
$ sudo ./dns-proxy-server
```
Now DNS Proxy Server is your DNS server, to back everything to original state just press <kbd>CTRL</kbd> + <kbd>C</kbd>;

[Click here][1] to see usage samples

## Running as Docker Service

If you are using docker on your machine that's the best choice as it will automatically start DPS on every boot:

```bash
$ docker run --hostname dns.mageddo --restart=unless-stopped -d \
  -p 5354:53/tcp \
  -p 5354:53/udp \
  -p 5380:5380 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  defreitas/dns-proxy-server
```
Explaining the running params:

`--network host`: Running on host mode make it possible to DPS bind the
DNS server port to the host network interface, this way all containers will have access to DPS address
and use DPS features.
If you don't want to use that option then you can consider use [DPS Network feature][2].

`/var/run/docker.sock`: Docker socket, so DPS can query the running containers and solve their IP when asked.

`5354:53` publishing on port 5354 instead of 53 to evict port conflicts

#### In case you do have `systemd-resolved` installed:

You can run DPS on different port if you are having conflicts as system-resolved suports custom ports.
But you will need to configure DPS as default DPS manually, check the instructions below:

Get your local network card IP, to list available networks run:
```bash
$ ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eno1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc fq_codel state UP group default qlen 1000
    link/ether e0:d5:5f:b7:8a:1a brd ff:ff:ff:ff:ff:ff
    altname enp0s31f6
    inet 192.168.0.128/24 brd 192.168.0.255 scope global dynamic noprefixroute eno1
       valid_lft 3011sec preferred_lft 3011sec
```
In my case the right Network is `en01` with  IP `192.168.0.128`,
`127.0.0.1` will partially works because this way containers won't be able
to solve each other, just the host solve the containers.

Edit systemd-resolved conf file with the found IP
```bash
$ cat /etc/systemd/resolved.conf 
...
[Resolve]
DNS=192.168.0.128:5354
```

Then restart the service:

```bash
$ service systemd-resolved restart
```

#### In case you don't have systemd-resolved installed:

If you don't have systemd-resolved installed then you can volume resolv.conf with the option `-v /etc/:/host/etc` 
when running the container but will need to publish DPS on port 53 instead of 5354,
don't use that if you have systemd-resolved installed because it will cause docker DNS misconfiguration.

#### Testing
Once configured, [click here][1] to see usage samples.

[1]: {{%relref "2-features/_index.md#main-features-use-cases" %}}
[2]: {{%relref "2-features/dps-network-resolution/_index.md" %}}
[3]: https://github.com/mageddo/dns-proxy-server/releases
