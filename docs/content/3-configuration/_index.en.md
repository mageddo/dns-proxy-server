---
title: Configuration
weight: 3
pre: "<b>3. </b>"
---

## Configs

### Remote DNS Servers
Remote DNS servers to be asked when can not solve from docker or local storage.
Default: `8.8.8.8`.

### Web Server Port
Web GUI port, Default: `5380`.

### DNS Server Port 
Default: `53`.

### Log Level
Default: `INFO`.

| Env            | JSON       | Terminal   |
|----------------|------------|------------|
| `MG_LOG_LEVEL` | `logLevel` | See --help |

### Log File
Where the log will be written. Default: console.

| Env           | JSON      | Terminal   |
|---------------|-----------|------------|
| `MG_LOG_FILE` | `logFile` | See --help |

### Register Container Names
If should register container name / service name as a hostname. Default: false.

| Env                           | JSON                     | Terminal   |
|-------------------------------|--------------------------|------------|
| `MG_REGISTER_CONTAINER_NAMES` | `registerContainerNames` | See --help |

### Domain
The container names domain used on the registered container, services. Default: `docker`.

Ex: 
```bash
docker run --rm --name nginx nginx
```
Will register a container with the name `nginx.docker`

| Env         | JSON     | Terminal   |
|-------------|----------|------------|
| `MG_DOMAIN` | `domain` | See --help |

### DPS Network
If should create a bridge network for dps container. Default: false.

### DPS Network Auto Connect
If should connect all containers to dps container so they can surely talk with each other. requires DPS Network Option.
Default: false.

### Default DNS
If DPS must be set as the default DNS automatically, commonly requires DPS be run as sudo/administrator permissions,
this options also won't work in some cases when running inside a docker container, [see the feature details][1].

### Host Machine Hostname 
Hostname to solve machine IP, domain can be changed by Domain option. Default: `host.docker`. 

| Env                        | JSON                  | Terminal   |
|----------------------------|-----------------------|------------|
| `MG_HOST_MACHINE_HOSTNAME` | `hostMachineHostname` | See --help |

### Server Protocol
Protocol to start the dns server. Default: `UDP_TCP`.

### Docker Host
Docker host address. Default value is SO dependent.

| Env              | JSON         | Terminal   |
|------------------|--------------|------------|
| `MG_DOCKER_HOST` | `dockerHost` | See --help |

### Resolvconf Override Name Servers
If must comment all existing nameservers at `resolv.conf` file (Linux, MacOS) or just put DPS at the first place. 
Default: true.

| Env                                  | JSON                            | Terminal   |
|--------------------------------------|---------------------------------|------------|
| `MG_RESOLVCONF_OVERRIDE_NAMESERVERS` | `resolvConfOverrideNameServers` | See --help |

### Resolvconf
Linux/Mac resolvconf or systemd-resolved path to set DPS as default DNS. 
Default: `/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf`.

| Env           | JSON | Terminal   |
|---------------|------|------------|
| MG_RESOLVCONF |      | See --help |

### No Remote Servers
If remote servers like 8.8.8.8 must be disabled and only local solvers like docker containers or local db must be used.
Default: false.

| Env                    | JSON              | Terminal   |
|------------------------|-------------------|------------|
| `MG_NO_REMOTE_SERVERS` | `noRemoteServers` | See --help |

### Active Env
Active Env used to query local db entries. Default `` (Empty String).

| Env | JSON        | Terminal |
|-----|-------------|----------|
|     | `activeEnv` |          |

### No Entries Response Code
Response code to use when no entries are returned by the configured solvers. Default: 3

| Env                         | JSON                    | Terminal  |
|-----------------------------|-------------------------|-----------|
| MG_NO_ENTRIES_RESPONSE_CODE | `noEntriesResponseCode` | See -help |

### Local Entries Solving (LocalDB)
See [Local Entries Solving][2] docs.

### Docker Solver Host Machine IP Fallback

Whether should answer host machine IP when a matching container is found but it hasn't
an IP to be answered, see Github Issue [#442](https://github.com/mageddo/dns-proxy-server/issues/442).
Default: true

| Env                                           | JSON                                    | Terminal  |
|-----------------------------------------------|-----------------------------------------|-----------|
| MG_DOCKER_SOLVER_HOST_MACHINE_FALLBACK_ACTIVE | `dockerSolverHostMachineFallbackActive` | See -help |

## Example JSON configuration

__Version 2__

```json
{
  "version": 2,
  "remoteDnsServers": [ "8.8.8.8", "4.4.4.4:54" ],
  "envs": [
    {
      "name": "", // empty string is the default enviroment
      "hostnames": [ // all local hostnames entries
        {
          "id": 1, // (optional) used to control it will be automatically generated if not passed
          "type": "A",
          "hostname": "github.com",
          "ip": "192.168.0.1",
          "ttl": 255 // how many seconds cache this entry
        }
      ]
    }
  ],
  "activeEnv": "", 
  "webServerPort": 5380, 
  "dnsServerPort": 53, 
  "logLevel": "INFO",
  "logFile": "console",
  "registerContainerNames": false, 
  "domain": "docker", 
  "dpsNetwork": false,
  "dpsNetworkAutoConnect": false, 
  "defaultDns": true,
  "hostMachineHostname" : "host.docker", 
  "serverProtocol": "UDP_TCP", 
  "dockerHost": null,
  "resolvConfOverrideNameServers": true,
  "noRemoteServers": false,
  "noEntriesResponseCode": 3,
  "dockerSolverHostMachineFallbackActive": true,
  "solverRemote" : {
    "circuitBreaker" : { 
      "failureThreshold" : 3, // how many attempts before open the circuit?
      "failureThresholdCapacity" : 10, // how many attempts store to the stack?
      "successThreshold" : 5, // how many attempts before close the circuit?
      "testDelay" : "PT20S" // how many time to wait before test the circuit again?, see https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#toString-- for format explanation
    }
  }
}
```

## Environment variable configuration

Boolean values

> You can use `1` or `true` (case insensitive) to specify which the flag is activated, any other
> value will be considered false.

## Terminal configuration
Run one of the commands below to get the commandline instructions help:

```bash
$ ./dns-proxy-server --help
```

```bash
$ docker run defreitas/dns-proxy-server --help
```

[1]: {{%relref "2-features/auto-configuration-as-default-dns/_index.md" %}}
[2]: {{%relref "2-features/local-entries/_index.md" %}}
