### Index
* Running
	* [Running on Linux](#running-on-linux)
	* [Running on Windows / MAC](#running-on-windows--mac)
* [Testing the DNS server](#testing-the-dns-server)
* [Installing it as a Linux service](#installing-it-as-a-linux-service)
* [File configuration/Terminal Options](#configure-your-dns)

### Running on Windows / MAC

> We have [cases](https://github.com/mageddo/dns-proxy-server/issues/66) of people got DPS running on Windows,
below the information we have of how to run DPS on these OS, if you confirm that or have some information that
would be helpful to archieve this please contribute creating a pull request or issue documenting that

> I'm not a Windows/MAC user then I will need help to get DPS totally functional at these platforms

1. Start up DPS

```bash
docker run --name dns-proxy-server -p 5380:5380 -p 53:53/udp \
  -v /var/run/docker.sock:/var/run/docker.sock \ 
  defreitas/dns-proxy-server
```

2. Change your default internet adapter DNS to `127.0.0.1`

**Windows documented only**

* Press `Windows + R` and type `ncpa.cpl` then press **enter** or go to your network interfaces Window
* Change your default internet adapterDNS to `127.0.0.1` by following the below 
pictures (sorry they are in portuguese)
 
[![](https://i.imgur.com/1goUHp0s.png)](https://i.imgur.com/1goUHp0.png)
[![](https://i.imgur.com/XKM7JwNs.png)](https://i.imgur.com/XKM7JwN.png)
[![](https://i.imgur.com/EFno6F6s.png)](https://i.imgur.com/EFno6F6.png)

### Running on Linux

__From docker__

```bash
$ docker run --rm --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  defreitas/dns-proxy-server
```

__Standalone run__

Download the [latest version](https://github.com/mageddo/dns-proxy-server/releases), extract and run

	$ sudo ./dns-proxy-server

Dns Proxy Server is now your current DNS server, to back everything to original state just press `CTRL + C`


### Testing the DNS server

Starting some docker container and keeping it alive for DNS queries

```bash
$ docker run -d --hostname nginx.dev.intranet \
  -e 'HOSTNAMES=nginx2.dev.intranet,nginx3.dev.intranet' \
  nginx
d96280ba54b44446f342ca78c0bc3b6b23efd78393d8e51e68757b5004314924
```

Solving the docker container hostname from Dns Proxy Server

```bash
$ nslookup nginx.dev.intranet
Server:		172.22.0.6
Address:	172.22.0.6#53

Non-authoritative answer:
Name:	debian.dev.intranet
Address: 172.22.0.7
```

Google keep working was well

```bash
$ nslookup google.com
Server:		172.22.0.6
Address:	172.22.0.6#53

Non-authoritative answer:
Name:	google.com
Address: 172.217.29.206
```

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

### Installing it as a Linux service

__Option 1__

```bash
docker run --hostname dns.mageddo --name dns-proxy-server -p 5380:5380 \
  --restart=unless-stopped -d \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  defreitas/dns-proxy-server
```

__Option 2__

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
