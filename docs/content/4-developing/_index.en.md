---
title: Developing
weight: 5
pre: "<b>5. </b>"
---

### Vanilla Developing

#### Backend

Play class `com.mageddo.dnsproxyserver.App` or run

```bash
$  ./gradlew clean shadowJar && java -jar ./build/libs/dns-proxy-server-*-all.jar 
```

Make your DNS queries to IP and TCP/UDP ports indicated at the console log.

Front end app (optional)

```bash
$ cd app && npm start
```

Then access http://localhost:3000/ , front end will proxy to http://localhost:5380 backend.

## With Docker

```bash
$ ./gradlew clean build shadowJar
$ docker-compose -f docker-compose-dev.yml up backend
```

or 

```bash
$ ./gradlew clean build shadowJar -i -x check
$ docker-compose -f docker-compose-dev.yml run --rm -it backend bash
$ java -jar dns-proxy-server-*-all.jar
```

## Releasing

Patch version
```bash
$ ./gradlew release
```

Major version
```bash
$ VERSION=3.7.0 && ./gradlew release -Prelease.releaseVersion=${VERSION} -Prelease.newVersion=${VERSION}
```
