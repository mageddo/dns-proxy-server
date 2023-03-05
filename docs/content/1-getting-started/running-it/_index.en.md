---
title: Running it
weight: 1
---

### Specific Instructions

{{%children style="li"  %}}

### General Instructions

The process to running DPS is basically the same for all platforms, except by Windows which don't support 
[auto configuration as default DNS][1].

Download the [latest release][2] for your platform

Start DPS
```bash
sudo ./dns-proxy-server
```

Then you can solve from pre-configured entries (conf/config.json):
```bash
$ ping dps-sample.dev
PING dps-sample.dev (192.168.0.254) 56(84) bytes of data.
```

Also solve Docker containers:
```bash
$ docker run --rm --hostname nginx.dev nginx

$ ping nginx.dev
PING nginx.dev (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4 (172.17.0.4): icmp_seq=1 ttl=64 time=0.043 ms
64 bytes from 172.17.0.4 (172.17.0.4): icmp_seq=2 ttl=64 time=0.022 ms
```

## Running on Docker

When you run DPS on docker it won't be able to configure itself as the default host DNS depending on your environment,
so you will need to that manually, see [specific running it][3] 
instructions for your platform for more details.

[1]: https://github.com/mageddo/dns-proxy-server/issues/326
[2]: https://github.com/mageddo/dns-proxy-server/releases
[3]: #specific-instructions
