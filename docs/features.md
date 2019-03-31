### Solve hostnames using wildcards

If you register a hostname with `.` at start, then all subdomains will solve to that container/local storage entry

Example

	docker run --rm --hostname .mageddo.com nginx:latest

Now all **mageddo.com** subdomains will solve to that nginx container

```
$ nslookup site1.mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	site1.mageddo.com
Address: 172.17.0.5

$ nslookup mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	mageddo.com
Address: 172.17.0.5

```

### Enable/Disable console log or change log path
You can disable, log to console, log to default log file path or specify a log path at config file, environment or command line argument. Available options:

* console (default) - log to console
* false - Logs are disabled
* true - stop log to console and log to `/var/log/dns-proxy-server.log` file
* <path> eg. /tmp/log.log - log to specified path

__Config File__
```json
{
	...
	"logFile": "console"
	...
}
```

__Environment__

	export MG_LOG_FILE=console

__Command line argument__

	go run dns.go  -log-file=console

### Set log level
You can change system log level using environment variable, config file, or command line argument, 
DPS will consider the parameters in that order, first is more important.
 
Available levels:

* ERROR
* WARNING
* INFO
* DEBUG (Default)

__Environment__

	export MG_LOG_LEVEL=DEBUG

__Config file__

```json
{
	...
	"logLevel": "DEBUG"
	...
}
```

__Command line argument__

	go run dns.go  -log-level=DEBUG


### Solve host machine IP from anywhere 

Just use `host.docker`

```bash
$ ping host.docker
PING host.docker (172.21.0.1) 56(84) bytes of data.
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=1 ttl=64 time=0.086 ms
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=2 ttl=64 time=0.076 ms
64 bytes from 172.21.0.1 (172.21.0.1): icmp_seq=3 ttl=64 time=0.081 ms
```

You can customize this hostname by setting 

Environment variable

```bash
$ docker run dns-proxy-server -e MG_HOST_MACHINE_HOSTNAME=$(cat /etc/hostname)
```

Command line option

```bash
$ ./dns-proxy-server --host-machine-hostname $(cat /etc/hostname)
```

Json configuration

```json
{
	"hostMachineHostname": "host.docker" 
}
```

**Notes**:

Be aware if you set the host machine hostname as the machine name then you will have to remove
it's name from `/etc/hosts` since OS try to resolve names from hosts file first
then from DNS server 

### Access container by it's container name / service name

```bash
$ docker run -rm nginx --name my-nginx
```

```bash
$ nslookup my-nginx.docker
Server:		172.17.0.3
Address:	172.17.0.3#53

Non-authoritative answer:
Name:	my-nginx.docker
Address: 10.0.2.3
```

You can enable this feature by 

__Activating by command line__

	./dns-proxy-server -register-container-names

__Configuring at json config file__

```
...
"registerContainerNames": true
...
```

__Using environment variable__

```bash
MG_REGISTER_CONTAINER_NAMES=1 ./dns-proxy-server
```

You can also  customize the domain from docker to whatever you want by
 
__Activating by command line__

	./dns-proxy-server --domain docker

__Configuring at json config file__

```
...
"domain": "docker"
...
```

__Using environment variable__

```bash
MG_DOMAIN=docker ./dns-proxy-server
```

### Specify from which network solve container IP
If your container have multiple networks then you can specify which network to use when solving IP by specifying `dps.network` label.

Creating a container with two networks attached to
```bash
$ docker network create --attachable network1
$ docker network create --attachable network2
$ docker run --name nginx1 --rm --label dps.network=network2 --hostname server1.acme.com --network network1 nginx
$ docker network connect network2 nginx1
```

Getting networks masks
```bash
$ docker network inspect -f "{{ .IPAM.Config }}" network1
[{172.31.0.0/16  172.31.0.1 map[]}]

$ docker network inspect -f "{{ .IPAM.Config }}" network2
[{192.168.16.0/20  192.168.16.1 map[]}]
```

Solving container IP checking that solved IP will be the respective to configured `dps.network` label
```bash
$ nslookup server1.acme.com
Server:		172.17.0.3
Address:	172.17.0.3#53

Non-authoritative answer:
Name:	server1.acme.com
Address: 192.168.16.2
```
