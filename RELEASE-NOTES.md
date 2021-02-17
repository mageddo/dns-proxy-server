### 2.19.0
* Support for absolute paths on config files (#188)

### 2.18.7
* Fixing docker image on latest version wasn't being updated

### 2.18.6
* Fixing gateway IP resolution when not in DPS network (#186)

### 2.18.5
* Fixing unnecessary stacktraces were being logged
* Answering NXDOMAIN when no answers were found
* Fixing logging file trace

### 2.18.4
* Bumping github-cli to fix releasing

### 2.18.3
* Resolving docker services using configured DPS domain
* Fix presence check of config setting "domain"
* Adding working around coordinates for resolv.conf at the docs
* Fixing releasing

### 2.18.2
* Fixing wrong mapping on `logLevel` property

### 2.18.1
* Change log level before try to log something

### 2.18.0
* Feature: Multiple environments, now you can setup a group of hostnames and save it to a environment, then you can 
create a new environment and switch between them, very useful when working on different contexts switching from QA to PROD,
for example, [see the docs](http://mageddo.github.io/dns-proxy-server/2.18/en/2-features/multiple-environments/)

### 2.17.4
* Clearing cache for resolvers when the config file is saved

### 2.17.3
* Separating the build image from final image, removing unnecessary bash command

### 2.17.2
* Fixing docker build was using deprecated apt-get option

### 2.17.1
* Reducing docker image size by 20%~

### 2.17.0
* Go version upgrade from 1.11 to 1.12

### 2.16.0
* Upgrading docker images to debian-10-slim
* Reducing up to 30% on image size

### 2.15.0
* Decreasing chance of acl issues by giving priority to answer ip of bridge networks over overlay ones
* Now DPS can have your own network this way it can access and be accessed
by all docker containers, **not** enabled by default [see the docs](http://mageddo.github.io/dns-proxy-server/2.15/en/2-features/dps-network-resolution/) 

### 2.14.6
* Fixing ping slowness

### 2.14.5
* Fixing docker hub push

### 2.14.4
* Fixing log level wasn't being respected 

### 2.14.2
* Ability to specify remote server port
* Introducing storage api v2
* Refactoring the docs to use Hugo templates 
 
### 2.14.1
* Fixing nil pointer when remote server get timeout (#126)
* Simplify bug report
* Fixing nil pointer when remote server returns timeout

### 2.14.0
* Making some refactoring facilitating to the feature requested at #121
* Fixing nil pointer sometimes when the hostname were not found

### 2.13.2
* Fixing broken answer when hostname is not found
* Fixing ping slowness
 
### 2.13.1
* Make sure value column will not break the table (#116)

### 2.13.0
* Support for CNAME on local entries, [see the docs](https://github.com/mageddo/dns-proxy-server/blob/7dacc2c/docs/features.md#manager-customer-dns-records)

### 2.12.0
* Possibility to change container hostname domain, [see the docs](https://github.com/mageddo/dns-proxy-server/blob/70a0ff8/docs/features.md#access-container-by-its-container-name--service-name)

### 2.11.0
* Now you can customize host machine hostname, see [the docs](https://github.com/mageddo/dns-proxy-server/blob/fa1e044b/docs/features.md#solve-host-machine-ip-from-anywhere)
* Increased default loglevel to INFO

### 2.10.3
* Build arm images on travis cause docker hub haven't support

### 2.10.2
* Fixing binaries were generated for wrong arch

### 2.10.1
* Official support for ARM

### 2.9.1
* Supporting Multilevel wildcard
* Fixing ping slowness, bug introduced on **2.9.0**

### 2.9.0
* Now remote resolved names are cached respecting TTL
* Refactored local storage cache

### 2.8.0
* If your container have multiple networks you can specify which network to use when solving IP by specifying `dps.network` label

### 2.7.0
* Now you can access your container by its container / docker-compose service name, syntax is `<container-name>.docker`

### 2.6.1
* Updating docs

### 2.6.0
* Now you can solve host machine IP from anywhere using host `host.docker`

### 2.5.4
* Organize some logs and auto reconfigure as default dns if resolvconf changes

### 2.5.3
* Fixing wildcard resolution were not solving main domain to local configuration, just the subdomains

### 2.5.2
* Fixing log level that stopped of work after **2.5.0**
* Fixing and increasing docs development instructions
* Fixing wildcard resolution were not solving main domain to docker container, just the subdomains

### 2.5.1
* Fixing ping slowness, takes more than 10 seconds to respond 

### 2.5.0
* Migrate to static logging tool

### 2.4.1
* Service restart command was with typo

### 2.4.0
* Enable/Disable log/set log path using `MG_LOG_FILE` env or `--log-file` command line option or json config
* Change log level using `MG_LOG_LEVEL` env or `--log-level` command line option or json config

### 2.3.3
* Domains wildcard support
If you register a hostname with `.` at start, then all subdomains will solve to that container/local storage entry

### 2.2.3
* Some times container hostname don't get registered at machine startup

### 2.2.2
* Cache Rest API v1 is exposed

### 2.2.1
* Preventing nil pointer when container inspection fails

### 2.2.0
* Increased code coverage
* Implementing cache at local hostnames and remote server resolution
* Considering TTL to invalidate hostname cache for local resolution

### 2.1.7
* All build and release process is made inside docker (no travis dependency)

### 2.1.6
* Refactor project structure to save dependencies in vendor folder

### 2.1.5
* Automating build with Travis

### 2.1.1
* Fix - `Error response from daemon: No such container...` message. see #29  
* Fix - hostname don't get removed when the container has killed. see #26  

### 2.1.0
* Turn publish port optional when running as service using docker mode

### 2.0.21
* BugFix - Service stopped of work in normal mode

### 2.0.20 
* Support for --version option that shows the current version
* Docker Compose is not required anymore to run DNS Proxy Server as a docker service

### 2.0.19
* Ability to customize remote server
* Fixing DNS solution order from (local, docker, remote) to (docker, local, remote)
* Now, at least docker 1.9 API v1.21 is necessary

### 2.0.18
* Making it compatible with docker 1.8 api v1.20
