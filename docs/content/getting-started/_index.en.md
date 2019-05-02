---
title: Getting Started
weight: 1
pre: "<b>1. </b>"
---

Running it

```bash
$ docker run --rm --hostname dns.mageddo \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /etc/resolv.conf:/etc/resolv.conf \
defreitas/dns-proxy-server
```

Try it out

```bash
$ ping dns.mageddo
PING dns.mageddo (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4: icmp_seq=1 ttl=64 time=0.063 ms
64 bytes from 172.17.0.4: icmp_seq=2 ttl=64 time=0.074 ms
64 bytes from 172.17.0.4: icmp_seq=3 ttl=64 time=0.064 ms
```

[Click here]({{%relref "getting-started/running-it/_index.en.md" %}}) to see more details about run DPS
