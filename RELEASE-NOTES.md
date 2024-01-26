## 3.16.1
* Bump Docker client API to fix compatibility with Docker version 25 #432

## 3.16
* Circuit Breaker for Remote DNS Servers #415

## 3.15
* Support for AAAA records on Docker container, LocalDB and System solving.
* AAAA GUI records support 
* DPS Network IPV6 Support
* Formalize Recursion Available on query responses as DPS supports this feature already, see #392
* Defined a new process of releasing the stable versions, see "Getting Started -> Release Version Control" 
for more details
* Fixed arm64 images stopped being pushed at `3.9`
* Cache improvement for a concurrent approach synchronized by key
* Increasing tcp and udp server parallelism from 20 to 50 threads each, threads are created lazily
* Fixing solver NPE on unsupported query types
* Option to not comment out existing nameservers from resolv.conf [see the docs][1]
* Option to disable remote solvers, see [the docs][2] for more details.
* Fixing DPS auto connect feature stops working suddenly, see #408
* Option to customize RCODE when no entries are found by all tried solvers [see the docs][3]

[1]: http://mageddo.github.io/dns-proxy-server/3.15/en/3-configuration/
[2]: https://mageddo.github.io/dns-proxy-server/3.15/en/3-configuration/#no-remote-servers
[3]: https://mageddo.github.io/dns-proxy-server/3.15/en/3-configuration/#no-entries-response-code

## 3.14.5
* Specify minimum required docker api version `1.21` (as DPS 2)

## 3.14.4
* Fixing regression on version 3.14 host.docker wasn't solving host IP when running inside a container, see #384.

## 3.14.3
* Support for regex on localdb solver and docker container hostname and HOSTNAMES env
* Smarter Cache for loading with Docker and LocalDB Solvers see #376
* Docker host parameter by flag, json config or env
* Full support for Docker containers solving on Windows Binary, DPS on docker already works on Windows
* Prefer to solve real network card address when querying for `host.docker`

## 3.13.1
* Caching remote solved hostnames for 5 minutes
* Caching not found hostnames for 1 hour
* Gui interface to clear the cache
* Created but not documented APIs to get cache size its and values
* Increased remote solver timeout to 10 seconds to make sure won't get easlily get timeout due to server slowness
* Ordering interfaces by index when choosing an IP as the machine IP, put loopback at the end of the list. 
* Holding TCP open connections up to 2min respecting RFC-1035 section "4.2.2. TCP usage"
  * Fixed too many occurrences of "java.net.SocketException: Socket closed"
* Limited TCP/UDP Server thread pool up to 20 threads due to control memory usage
* Log level adjustments

## 3.12.1
* Binding UDP server to anylocalhost just as TCP this way DPS can be used on any interface.
* Disabling mac as the binary isn't working macos-latest, see #341
* Fixing DPS network features startup

## 3.12
* Also configuring resolv.conf on Mac when in standalone mode
* Fixing random failure on dns configs restore on MacOSX

## 3.11.0
* Windows and MacOs binary releases (beta)

## 3.9.2
* This version is focused on Windows and MacOS binary release

## 3.9.1
* Now, releasing the latest docker image

## 3.9.0
* Support to configure DPS as default DNS Server on Windows.
* Fixed previous DNS servers restore on Mac.

## 3.8.1
* Fixing `SIGTERM` signal wasn't being respected at Linux image

## 3.8.0
* Now you're able to activate flags by using `1` or `true` (case insenstive), [see the docs](http://mageddo.github.io/dns-proxy-server/latest/en/3-configuration/#environment-variable-configuration).

## 3.7.0
* Support to configure DPS as default DNS Server on MacOS, [see the docs](http://mageddo.github.io/dns-proxy-server/latest/en/1-getting-started/running-it/#running-on-mac)

## 3.6.0
* Configure DPS at system-resolved with a custom port when is necessary
* Auto DNS configurator will give up to configure after 3 failures

## 3.5.3
* LocalSolverDB Wildcards fix: wasn't working
* LocalSolverDB Case sensitive query fixes: camelCase hostnames weren't being solved

## 3.5.0
* Final release
* Add local entry `dps-sample.dev` so people can better understand how this feature works

## 3.4.0-beta

* DPS will detect and configure systemd-resolved when available
* The default value of MG_RESOLVCONF was changed to
```
/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf
```
See issue [#321](https://github.com/mageddo/dns-proxy-server/issues/321) for more details.

## 3.3.0-beta
### MG_RESOLVCONF will now accept more than one value
They will separate by comma , DPS will look for each value, try to use it and stops when finds a valid value, some path which is able to configure (an existing path, with the right read and write permissions and parseable by DPS)

### The default value of MG_RESOLVCONF was changed to
```
/host/etc/resolv.conf,/etc/resolv.conf
```

## 3.2.5-beta
* Be able to run in docker container in network host mode
* When finding network by name must find with the exact name

## 3.2.3-beta
* Fixed docker container wasn't solving from env when the names weren't separated by ` , ` 
 (spaces before and after the comma were needed)

## 3.2.2-beta
* Fixed DPS container was connecting to DPS network with wrong IP 

## 3.2.1-beta
* Respecting OS to configure as default DNS
* Other minor fixes 

## 3.2.0-beta
* Better error treating and log formatting
* TCP Server: Ensure will read the header before go to next step

## 3.1.7-beta
* TCP Server Partially message read fix, fixing `status=headerMsgSizeDifferentFromReadBytes!` message

## 3.1.6-beta
* Work only with up interfaces
* Re-activate loopback interfaces

## 3.1.5-beta
* Fixing UDP binding server to any interface on the machine wasn't working for some clients
 
## 3.1.4-beta
* Binding UDP server to any interface on the machine
* Fallback to 127.0.0.1 interface when the real one is missing

## 3.1.3-beta
* Doc adjustments
* Fixing doc broken pages due hugo previous upgrade
* Fixing docker container wildcard solving
* Prioritize to [solve bridge networks over overlay ones](https://github.com/mageddo/dns-proxy-server/blob/cce3926837add0ea661648cd534c9a1192d171e1/src/test/java/com/mageddo/dnsproxyserver/docker/DockerServiceTest.java#L52)

## 3.1.2-beta
DPS 3 -  Minor fixes

* Timed out remote servers responses are being cached, it cant happen, when its a timeout cant cache but when the record really dont exists, like AAAA for bookmarks.mageddo.com must cache.
* Must read conf from dps binary path not the current os path where dps was called
* Log level not being respected, at least no when running in native image binary
* Removed quarkus splash logo

## 3.1.1-beta
* Fixing reponse warning `;; Warning: query response not set`

## 3.1.0-beta
* TCP Protocol support

## 3.0.4-beta
* Generating new distributions binaries
  * aarch64 aside docker image
  * uber jar, target jvm = 17 for now

## 3.0.2-beta
* Fixed home page was pointing to wrong place

## 3.0.1-beta
* DPS has your code totally refactored by maintaining the previous features, it's a structuring for new features
 See details at [DNS Proxy Server 3 #267](https://github.com/mageddo/dns-proxy-server/issues/267)

## 2.19.0
* Support for absolute paths on config files (#188)

## 2.18.7
* Fixing docker image on latest version wasn't being updated

## 2.18.6
* Fixing gateway IP resolution when not in DPS network (#186)

## 2.18.5
* Fixing unnecessary stacktraces were being logged
* Answering NXDOMAIN when no answers were found
* Fixing logging file trace

## 2.18.4
* Bumping github-cli to fix releasing

## 2.18.3
* Resolving docker services using configured DPS domain
* Fix presence check of config setting "domain"
* Adding working around coordinates for resolv.conf at the docs
* Fixing releasing

## 2.18.2
* Fixing wrong mapping on `logLevel` property

## 2.18.1
* Change log level before try to log something

## 2.18.0
* Feature: Multiple environments, now you can setup a group of hostnames and save it to a environment, then you can 
create a new environment and switch between them, very useful when working on different contexts switching from QA to PROD,
for example, [see the docs](http://mageddo.github.io/dns-proxy-server/2.18/en/2-features/multiple-environments/)

## 2.17.4
* Clearing cache for resolvers when the config file is saved

## 2.17.3
* Separating the build image from final image, removing unnecessary bash command

## 2.17.2
* Fixing docker build was using deprecated apt-get option

## 2.17.1
* Reducing docker image size by 20%~

## 2.17.0
* Go version upgrade from 1.11 to 1.12

## 2.16.0
* Upgrading docker images to debian-10-slim
* Reducing up to 30% on image size

## 2.15.0
* Decreasing chance of acl issues by giving priority to answer ip of bridge networks over overlay ones
* Now DPS can have your own network this way it can access and be accessed
by all docker containers, **not** enabled by default [see the docs](http://mageddo.github.io/dns-proxy-server/2.15/en/2-features/dps-network-resolution/) 

## 2.14.6
* Fixing ping slowness

## 2.14.5
* Fixing docker hub push

## 2.14.4
* Fixing log level wasn't being respected 

## 2.14.2
* Ability to specify remote server port
* Introducing storage api v2
* Refactoring the docs to use Hugo templates 
 
## 2.14.1
* Fixing nil pointer when remote server get timeout (#126)
* Simplify bug report
* Fixing nil pointer when remote server returns timeout

## 2.14.0
* Making some refactoring facilitating to the feature requested at #121
* Fixing nil pointer sometimes when the hostname were not found

## 2.13.2
* Fixing broken answer when hostname is not found
* Fixing ping slowness
 
## 2.13.1
* Make sure value column will not break the table (#116)

## 2.13.0
* Support for CNAME on local entries, [see the docs](https://github.com/mageddo/dns-proxy-server/blob/7dacc2c/docs/features.md#manager-customer-dns-records)

## 2.12.0
* Possibility to change container hostname domain, [see the docs](https://github.com/mageddo/dns-proxy-server/blob/70a0ff8/docs/features.md#access-container-by-its-container-name--service-name)

## 2.11.0
* Now you can customize host machine hostname, see [the docs](https://github.com/mageddo/dns-proxy-server/blob/fa1e044b/docs/features.md#solve-host-machine-ip-from-anywhere)
* Increased default loglevel to INFO

## 2.10.3
* Build arm images on travis cause docker hub haven't support

## 2.10.2
* Fixing binaries were generated for wrong arch

## 2.10.1
* Official support for ARM

## 2.9.1
* Supporting Multilevel wildcard
* Fixing ping slowness, bug introduced on **2.9.0**

## 2.9.0
* Now remote resolved names are cached respecting TTL
* Refactored local storage cache

## 2.8.0
* If your container have multiple networks you can specify which network to use when solving IP by specifying `dps.network` label

## 2.7.0
* Now you can access your container by its container / docker-compose service name, syntax is `<container-name>.docker`

## 2.6.1
* Updating docs

## 2.6.0
* Now you can solve host machine IP from anywhere using host `host.docker`

## 2.5.4
* Organize some logs and auto reconfigure as default dns if resolvconf changes

## 2.5.3
* Fixing wildcard resolution were not solving main domain to local configuration, just the subdomains

## 2.5.2
* Fixing log level that stopped of work after **2.5.0**
* Fixing and increasing docs development instructions
* Fixing wildcard resolution were not solving main domain to docker container, just the subdomains

## 2.5.1
* Fixing ping slowness, takes more than 10 seconds to respond 

## 2.5.0
* Migrate to static logging tool

## 2.4.1
* Service restart command was with typo

## 2.4.0
* Enable/Disable log/set log path using `MG_LOG_FILE` env or `--log-file` command line option or json config
* Change log level using `MG_LOG_LEVEL` env or `--log-level` command line option or json config

## 2.3.3
* Domains wildcard support
If you register a hostname with `.` at start, then all subdomains will solve to that container/local storage entry

## 2.2.3
* Some times container hostname don't get registered at machine startup

## 2.2.2
* Cache Rest API v1 is exposed

## 2.2.1
* Preventing nil pointer when container inspection fails

## 2.2.0
* Increased code coverage
* Implementing cache at local hostnames and remote server resolution
* Considering TTL to invalidate hostname cache for local resolution

## 2.1.7
* All build and release process is made inside docker (no travis dependency)

## 2.1.6
* Refactor project structure to save dependencies in vendor folder

## 2.1.5
* Automating build with Travis

## 2.1.1
* Fix - `Error response from daemon: No such container...` message. see #29  
* Fix - hostname don't get removed when the container has killed. see #26  

## 2.1.0
* Turn publish port optional when running as service using docker mode

## 2.0.21
* BugFix - Service stopped of work in normal mode

## 2.0.20 
* Support for --version option that shows the current version
* Docker Compose is not required anymore to run DNS Proxy Server as a docker service

## 2.0.19
* Ability to customize remote server
* Fixing DNS solution order from (local, docker, remote) to (docker, local, remote)
* Now, at least docker 1.9 API v1.21 is necessary

## 2.0.18
* Making it compatible with docker 1.8 api v1.20

## Notes
* compliance with: https://keepachangelog.com/en/1.0.0/
