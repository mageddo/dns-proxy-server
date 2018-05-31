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

__ Command line argument__

	go run dns.go  -log-level=DEBUG

