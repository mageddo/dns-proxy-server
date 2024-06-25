---
title: Running it
weight: 1
---

### Specific Instructions

{{%children style="li"  %}}

### Running as Service

If you are using Docker on your machine I recommend to run DPS as a docker service,
this way it will automatically be configured whenever you restart the machine,
checkout the instructions for [Linux][6], [Mac][7] and [Windows][5]

### General Instructions
The process to running DPS is basically the same for all platforms:

Download the [latest release][2] for your platform, if no binary available use the jar (requires JRE 21+)

Start DPS (you need to run as administrator/sudo so DPS can set itself as the default DNS)
```bash
sudo ./dns-proxy-server
```

Then you can solve from pre-configured entries (conf/config.json):
```bash
$ ping dps-sample.dev
PING dps-sample.dev (192.168.0.254) 56(84) bytes of data.
```

Also solve Docker containers*:
```bash
$ docker run --rm --hostname nginx.dev nginx

$ ping nginx.dev
PING nginx.dev (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4 (172.17.0.4): icmp_seq=1 ttl=64 time=0.043 ms
64 bytes from 172.17.0.4 (172.17.0.4): icmp_seq=2 ttl=64 time=0.022 ms
```

*Not supported on Windows


### Running on Docker

The standalone way to run DPS on docker can be done by following:

```bash
$ docker run --rm --hostname dns.mageddo -p 5380:5380 -p 53:53/udp defreitas/dns-proxy-server
```

If you get something like `0.0.0.0:53: bind: address already in use` then probably there is some other DNS server like 
systemd-resolved, so you can try to bind DPS to a specific network interface like `127.0.0.1` or `192.168.x.x`, check
your local interfaces for the best fit.

```bash
$ docker run --rm --hostname dns.mageddo -p 5380:5380 -p 127.0.0.1:53:53/udp defreitas/dns-proxy-server
```

You probably want to check on [specific OS running instructions][3] to configure all DPS features, when running on docker.

Running on docker limitations:

* You have to configure docker as default DNS manually, check [specific instructions][3],
to see how to do that you can check more details [here][4].

[1]: https://github.com/mageddo/dns-proxy-server/issues/326
[2]: https://github.com/mageddo/dns-proxy-server/releases
[3]: #specific-instructions
[4]: {{%relref "2-features/auto-configuration-as-default-dns/_index.md#docker-limitations" %}}
[5]: {{%relref "1-getting-started/running-it/windows/_index.md#running-as-docker-service" %}}
[6]: {{%relref "1-getting-started/running-it/linux/_index.md#running-as-docker-service" %}}
[7]: #
