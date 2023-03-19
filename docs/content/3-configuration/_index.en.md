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
  "webServerPort": 5380, // web admin port, when null the default value is used, see --help option
  "dnsServerPort": 53, // dns server port, when null the default value is used
  "logLevel": "INFO",
  "logFile": "console" // where the log will be written,
  "registerContainerNames": false, // if should register container name / service name as a hostname
  "domain": "docker", // The container names domain
  "dpsNetwork": false, // if should create a bridge network for dps container
  "dpsNetworkAutoConnect": false, // if should connect all containers to dps container
  "defaultDns" : true, // if must be set as the default DNS
  "hostMachineHostname" : "host.docker", // hostname to solve machine IP
  "serverProtocol" : "UDP_TCP", // protocol to start the dns server
  "dockerHost" : null, // docker host address, default value is SO dependent
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
| MG_DOCKER_HOST              | Docker host address                                                   | depends on the SO                                                                                 |

### Terminal configuration
Run one of the commands below to get the commandline instructions help:

```bash
$ ./dns-proxy-server --help
```

```bash
$ docker run defreitas/dns-proxy-server --help
```
