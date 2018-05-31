<p>
	<a href="https://travis-ci.org/mageddo/dns-proxy-server"><img src="https://travis-ci.org/mageddo/dns-proxy-server.svg?branch=master" alt="Build Status"></img></a>
</p>

### [Features](http://mageddo.github.io/dns-proxy-server/docs/documentation)
* [Take a look at documentation for more details](http://mageddo.github.io/dns-proxy-server/docs/documentation)

Dns-proxy-server is a end user(developers, Server Administrators) DNS server tool to develop systems with docker solving docker containers hostnames:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured DNS servers(as a proxy) if no answer of two above
* [Solve hostnames using wildcards](http://mageddo.github.io/dns-proxy-server/docs/documentation#Solve-hostnames-using-wildcards)
* [Graphic interface to manage it](http:/127.0.0.1:5380/static/)
	* List and edit DNS local entries

**For more details see** [Release Notes](RELEASE-NOTES.md)

![](http://i.imgur.com/Bhe9P36.png)

### Requirements
* Linux
* Docker 1.9.x

### DNS resolution order
The Dns Proxy Server basically follow the bellow order to solve the names:

* DNS try to solve the hosts from **docker** containers
* then from local database file
* then from 3rd configured remote DNS servers

### Running

##### From docker

	$ docker run --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
    -v /opt/dns-proxy-server/conf:/app/conf \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /etc/resolv.conf:/etc/resolv.conf \
    defreitas/dns-proxy-server

##### Standalone run

Download the [latest version](https://github.com/mageddo/dns-proxy-server/releases), extract and run

	$ sudo ./dns-proxy-server

Dns Proxy Server is now your current DNS server, to back everything to original state just press `CTRL + C`

##### Testing the DNS server

Starting some docker container and keeping it alive for DNS queries

```bash
$ docker run -d --hostname debian.dev.intranet \
  -e 'HOSTNAMES=debian2.dev.intranet,debian3.dev.intranet' \
  debian sleep infinity
d96280ba54b44446f342ca78c0bc3b6b23efd78393d8e51e68757b5004314924
```

Solving the docker container hostname from Dns Proxy Server

	$ nslookup debian.dev.intranet
	Server:		172.22.0.6
	Address:	172.22.0.6#53

	Non-authoritative answer:
	Name:	debian.dev.intranet
	Address: 172.22.0.7

Google keep working was well

	$ nslookup google.com
	Server:		172.22.0.6
	Address:	172.22.0.6#53

	Non-authoritative answer:
	Name:	google.com
	Address: 172.217.29.206
	
Start the server at [custom port](#configure-your-dns) and solving from it

	nslookup -port=8980 google.com 127.0.0.1

### Configure your DNS

./conf/config.json

```javascript
{
  "remoteDnsServers": [ [8,8,8,8], [4,4,4,4] ], // Remote DNS servers to be asked when can not solve from docker or local storage 
                                                // If no one server was specified then the 8.8.8.8 will be used
  "envs": [ // all existent environments 
    {
      "name": "", // empty string is the default
      "hostnames": [ // all local hostnames entries
        {
          "id": 1,
          "hostname": "github.com",
          "ip": [192, 168, 0, 1],
          "ttl": 255
        }
      ]
    }
  ],
  "activeEnv": "", // the default env keyname 
  "lastId": 1, // hostnames sequence, don't touch here
  "webServerPort": 0, // web admin port, when 0 the default value is used, see --help option
  "dnsServerPort": 8980, // dns server port, when 0 the default value is used
  "logLevel": "DEBUG",
  "logFile": "console" // where the log will be written
}
```

### If you need terminal options 

```
  -compress
    	compress replies
  -conf-path string
    	The config file path  (default "conf/config.json")
  -cpuprofile string
    	write cpu profile to file
  -default-dns
    	This DNS server will be the default server for this machine (default true)
  -help
    	This message
  -log-file string
    	Log to file instead of console, (true=log to default log file, /tmp/log.log=log to custom log location) (default "console")
  -log-level string
    	Log Level ERROR, WARNING, INFO, DEBUG (default "DEBUG")
  -server-port int
    	The DNS server to start into (default 53)
  -service string
    	Setup as service, starting with machine at boot
		docker = start as docker service,
		normal = start as normal service,
		uninstall = uninstall the service from machine 
  -service-publish-web-port
    	Publish web port when running as service in docker mode (default true)
  -tsig string
    	use MD5 hmac tsig: keyname:base64
  -version
    	Current version
  -web-server-port int
    	The web server port (default 5380)
```

### Installing it as a service

1. Download the [latest release](https://github.com/mageddo/dns-proxy-server/releases) and extract it
2. Run the service installer
```
$ sudo ./dns-proxy-server -service=docker
```
3. Then follow the progress at the log file
```
$ tail -f /var/log/dns-proxy-server.log 
```

this way it will start with the **OS**

if you want to stop 

	$ sudo service dns-proxy-server stop
	Stopping service…
	Service stopped

if you don't want this service anymore

	$ sudo service dns-proxy-server uninstall
	Are you really sure you want to uninstall this service? That cannot be undone. [yes|No] 
	yes


### Rest API

* [Latest API documentation](https://github.com/mageddo/dns-proxy-server/tree/master/docs/api) of DNS proxy server APIs

### Developing 
Take a look at the [wiki](docs) for more details of how develop at this project
