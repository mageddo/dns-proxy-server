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

![](https://i.imgur.com/aR9dl0O.png)

### Requirements
* Linux/Windows
* Docker 1.9.x (Only if you run DPS using docker or want to solve docker containers hostname using DPS)

### DNS resolution order
DNS  Proxy Server follow the below order to solve hostnames

* Try to solve the hostname from **docker** containers
* Then from local database file
* Then from 3rd configured remote DNS servers

### Documents
* [Running it on Linux/Windows](http://mageddo.github.io/dns-proxy-server/docs/running.html)
* [Latest Rest API Features](http://mageddo.github.io/dns-proxy-server/docs/api/)
* [Coding](docs/developing) at DNS Proxy Server

### MAC Support
Based on users feedback, DPS don't work on MAC, unfortunatly I don't have a MAC computer to work on that, if you want to contribute please try to fix it then open a pull request, thanks.
