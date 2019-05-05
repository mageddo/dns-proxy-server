---
title: Configuration
weight: 3
pre: "<b>3. </b>"
---

### JSON configuration

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

### Terminal configuration

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
