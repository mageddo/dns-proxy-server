<p>
	<a href="https://travis-ci.org/mageddo/dns-proxy-server"><img src="https://travis-ci.org/mageddo/dns-proxy-server.svg?branch=master" alt="Build Status" /></a>
</p>

### Features

DPS is a end user(developers, Server Administrators) DNS server tool to develop systems with docker solving
docker containers hostnames:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured DNS servers(as a proxy) if no answer of two above
* [Solve hostnames using wildcards](http://mageddo.github.io/dns-proxy-server/docs/features#solve-hostnames-using-wildcards)
* [Graphic interface to manage it](http:/127.0.0.1:5380/static/)
	* List and edit DNS local entries
* [Solve host machine IP using `host.docker` hostname](http://mageddo.github.io/dns-proxy-server/docs/features#solve-host-machine-ip-from-anywhere)

**For more details see** [the Documentation ](http://mageddo.github.io/dns-proxy-server/docs/features) or [Release Notes](RELEASE-NOTES.md) 

![](https://i.imgur.com/aR9dl0O.png)

### Running it

```bash
$ docker run --rm --hostname dns.mageddo \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /etc/resolv.conf:/etc/resolv.conf \
defreitas/dns-proxy-server
```

then try it out

```bash
$ ping dns.mageddo
PING dns.mageddo (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4: icmp_seq=1 ttl=64 time=0.063 ms
64 bytes from 172.17.0.4: icmp_seq=2 ttl=64 time=0.074 ms
64 bytes from 172.17.0.4: icmp_seq=3 ttl=64 time=0.064 ms
```

[Checkout the full running it documentation for more details](http://mageddo.github.io/dns-proxy-server/docs/running.html)

### Requirements
* Linux/Windows
* Docker 1.9.x (Only if you run DPS using docker or want to solve docker containers hostname using DPS)

### DNS resolution order
DNS  Proxy Server follow the below order to solve hostnames

* Try to solve the hostname from **docker** containers
* Then from local database file
* Then from 3rd configured remote DNS servers

### Documents
* [Latest Rest API Features](http://mageddo.github.io/dns-proxy-server/docs/api/)
* [Coding](docs/developing) at DNS Proxy Server

### MAC Support
Based on users feedback, DPS don't work on MAC, unfortunatly I don't have a MAC computer to work on that, 
if you want to contribute please try to fix it then open a pull request, sorry for the inconvenience.
