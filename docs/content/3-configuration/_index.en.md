---
title: Configuration
weight: 3
pre: "<b>3. </b>"
---

### JSON configuration

__Version 2__

```json
{
  "version": 2,
  // Remote DNS servers to be asked when can not solve from docker or local storage
  // If no one server was specified then the 8.8.8.8 will be used
  "remoteDnsServers": [ "8.8.8.8", "4.4.4.4:54" ],

  // all existent environments  
  "envs": [
    {
      "name": "", // empty string is the default enviroment
      "hostnames": [ // all local hostnames entries
        {
          // (optional) used to control it will be automatically generated if not passed
          "id": 1,
          "hostname": "github.com",
          "ip": "192.168.0.1",
          "ttl": 255 // how many seconds cache this entry
        }
      ]
    }
  ],
  "activeEnv": "", // the current environment keyname 
  "webServerPort": 0, // web admin port, when 0 the default value is used, see --help option
  "dnsServerPort": 8980, // dns server port, when 0 the default value is used
  "logLevel": "DEBUG",
  "logFile": "console" // where the log will be written,
  "registerContainerNames": false, // if should register container name / service name as a hostname
  "domain": "", // The container names domain
  "dpsNetwork": false, // if should create a bridge network for dps container
  "dpsNetworkAutoConnect": false // if should connect all containers to dps container
}
```

__Version 1__

```json
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
  "logFile": "console" // where the log will be written,
  "registerContainerNames": false // if should register container name / service name as a hostname
}
```

### Environment variable configuration

Boolean values
> You can use `1` or `true` (case insensitive) to specify which the flag is activated, any other
value will be considered false.

| VARIBLE                     | DESCRIPTION                                                           | DEFAULT VALUE                                                                                     |
|-----------------------------|-----------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| MG_RESOLVCONF               | Linux resolvconf or systemd-resolved path to set DPS as default DNS   | /host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf |
| MG_LOG_LEVEL                |                                                                       | INFO                                                                                              |
| MG_LOG_FILE                 | Path where to logs will be stored                                     | console                                                                                           |
| MG_REGISTER_CONTAINER_NAMES | if should register container name / service name as a hostname        | false                                                                                             |
| MG_HOST_MACHINE_HOSTNAME    | hostname to solve host machine IP                                     | host.docker                                                                                       |
| MG_DOMAIN                   | The container names domain (requires MG_REGISTER_CONTINER_NAMES=TRUE) | .docker                                                                                           |

### Terminal configuration

```
Usage: <main class> [-default-dns] [-dps-network] [-dps-network-auto-connect] [-help] [-register-container-names]
                    [-service-publish-web-port] [-version] [-conf-path=<configPath>] [-domain=<domain>]
                    [-host-machine-hostname=<hostMachineHostname>] [-log-file=<logToFile>] [-log-level=<logLevel>]
                    [-server-port=<dnsServerPort>] [-service=<service>] [-web-server-port=<webServerPort>]
      -conf-path, --conf-path=<configPath>
                            The config file path (default conf/config.json)
      -default-dns, --default-dns
                            This DNS server will be the default server for this machine (default true)
      -domain, --domain=<domain>
                            Domain utilized to solver containers and services hostnames (default docker)
      -dps-network, --dps-network
                            Create a bridge network for DPS increasing compatibility (default false)
      -dps-network-auto-connect, --dps-network-auto-connect
                            Connect all running and new containers to the DPS network,
                            this way you will probably not have resolution issues by acl (implies dps-network=true)
                            (default false)

      -help, --help         This message (default false)
      -host-machine-hostname, --host-machine-hostname=<hostMachineHostname>
                            The hostname to get host machine IP (default host.docker)
      -log-file, --log-file=<logToFile>
                            Log to file instead of console,
                            (true=log to default log file, /tmp/log.log=log to custom log location)
                            (default console)

      -log-level, --log-level=<logLevel>
                            Log Level ERROR, WARNING, INFO, DEBUG (default INFO)
      -register-container-names, --register-container-names
                            If must register container name / service name as host in DNS server (default false)
      -server-port, --server-port=<dnsServerPort>
                            The DNS server to start into (default 53)
      -service, --service=<service>
                            Setup as service, starting with machine at boot
                               docker = start as docker service,
                               normal = start as normal service,
                               uninstall = uninstall the service from machine
                            (default <empty>)

      -service-publish-web-port, --service-publish-web-port
                            Publish web port when running as service in docker mode (default true)
      -version, --version   Shows the current version (default false)
      -web-server-port, --web-server-port=<webServerPort>
                            The web server port (default 5380)
```
