### Enable/Disable console log or change log path
You can disable, log to console, log to default log file path or specify a log path at config file, environment or command line argument. Available options:

* console (default) - log to console
* false - Logs are disabled
* true - stop log to console and log to `/var/log/dns-proxy-server.log` file
* <path> eg. /tmp/log.log - log to specified path

#### Config File
```json
{
	...
	"logFile": "console"
	...
}
```

#### Environment

	export MG_LOG_FILE=console

#### Command line argument

	go run dns.go  -log-file=console

### Set log level
You can change system log level at config file, environment or command line argument. Available levels:

* ERROR
* WARNING
* INFO
* DEBUG (Default)

#### Config file
```json
{
	...
	"logLevel": "DEBUG"
	...
}
```

#### Environment

	export MG_LOG_LEVEL=DEBUG

#### Command line argument

	go run dns.go  -log-level=DEBUG

