<p>
	<a href="https://travis-ci.org/mageddo/dns-proxy-server"><img src="https://travis-ci.org/mageddo/dns-proxy-server.svg?branch=master" alt="Build Status"></img></a>
</p>

### Features
Dns-proxy-server is a end user(developers, Server Administrators) DNS server tool to develop systems with docker solving docker containers hostnames:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured DNS servers(as a proxy) if no answer of two above
* [Solve hostnames using wildcards](http://mageddo.github.io/dns-proxy-server/docs/features#Solve-hostnames-using-wildcards)
* [Graphic interface to manage it](http:/127.0.0.1:5380/static/)
	* List and edit DNS local entries

**For more details see** [the Documentation ](http://mageddo.github.io/dns-proxy-server/docs/features) or [Release Notes](RELEASE-NOTES.md) 

![](https://i.imgur.com/ojOQfRj.png)

### Requirements
* Linux/Windows/MAC
* Docker 1.9.x

### DNS resolution order
The Dns Proxy Server follow the below order to solve hostnames

* DNS try to solve the hosts from **docker** containers
* then from local database file
* then from 3rd configured remote DNS servers

### Documents
* [Running it on Linux/Windows/MAC](http://mageddo.github.io/dns-proxy-server/docs/api/running.html)
* [Latest Rest API Features](http://mageddo.github.io/dns-proxy-server/docs/api/)
* Take a look [developing section](docs/developing) for more details of how develop at this project
