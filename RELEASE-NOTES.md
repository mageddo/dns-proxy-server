### 2.8.0
* If your container have multiple networks you can specify which network to use when solving IP by specifying `dps.network` label

### 2.7.0
* Now you can access your container by it's container / docker-compose service name, syntax is `<container-name>.docker`

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
