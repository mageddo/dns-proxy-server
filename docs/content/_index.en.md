---
title: "Getting Started"
---

### Main features

DPS is a lightweight end user (Developers, Server Administrators) DNS server tool
which make it easy to develop in systems where one hostname can solve to different IPs based
on the configured environment, so you can:

* Solve hostnames from local configuration database
* Solve hostnames from [docker containers][1]
* Solve hostnames from a list of configured remote DNS servers(as a proxy) if no answer of two above
* Solve hostnames using wildcards
* Graphic interface to Create/List/Update/Delete **A/CNAME** records
* Solve host machine IP using `host.docker` hostname


![](https://i.imgur.com/aR9dl0O.png?width=60pc)

[1]: {{%relref "2-features/docker-solving/_index.md" %}}
