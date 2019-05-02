---
title: Compiling from source
weight: 4
pre: "<b>4. </b>"
---

### Building from source with docker

**DPS** uses docker to simplify the compile process

Generate the binaries

```bash
$ docker-compose rm -f && docker-compose up --build prod-build-binary-dps
Starting docker-dns-server-compiler
Attaching to docker-dns-server-compiler
docker-dns-server-compiler           | ok  	github.com/mageddo/dns-proxy-server/conf	0.008s
docker-dns-server-compiler           | ?   	github.com/mageddo/dns-proxy-server/controller	[no test files]
...
docker-dns-server-compiler           | github.com/mageddo/dns-proxy-server/flags
...
docker-dns-server-compiler           | _/app/src
docker-dns-server-compiler exited with code 0
```

Then binaries mus be available at **build** folder

```
$ ls build/
dns-proxy-server dns-proxy-server-2.5.1.tgz  static
``` 

If you want you can build the docker image

```
$ docker-compose build prod-build-image-dps && docker-compose up prod-dps
```

Take a look at docker-compose.yml for prod-build-image-dps* to build other arch 

### Building binary for specific ARCH

You can generate DPS binaries for [all archs that are supported by golang](https://golang.org/doc/install/source#environment),
DPS were designed to run on linux though.

```bash
$ docker-compose run prod-build-binary-dps builder.bash build linux amd64
> Testing
> Tests completed
> Building...
> Compiling os=linux, arch=amd64
```

### Used technologies 

* Docker
* Docker Compose
* Golang
