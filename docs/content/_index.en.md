---
title: "Getting Started"
---

![](https://i.imgur.com/aR9dl0O.png?width=60pc)

## Main features

DPS is a end user(developers, Server Administrators) DNS server tool to develop systems with docker solving
docker containers hostnames:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured DNS servers(as a proxy) if no answer of two above
* [Solve hostnames using wildcards](http://mageddo.github.io/dns-proxy-server/docs/features#solve-hostnames-using-wildcards)
* [Graphic interface to manage it](http:/127.0.0.1:5380/static/)
	* List and edit DNS local entries
* [Solve host machine IP using `host.docker` hostname](http://mageddo.github.io/dns-proxy-server/docs/features#solve-host-machine-ip-from-anywhere)
* [Access container by it’s container name / service name](http://mageddo.github.io/dns-proxy-server/docs/features#access-container-by-its-container-name--service-name)
* [Specify from which network solve container IP](http://mageddo.github.io/dns-proxy-server/docs/features#specify-from-which-network-solve-container-ip)
