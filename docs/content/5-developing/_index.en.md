---
title: Developing at the project
weight: 5
pre: "<b>5. </b>"
---

### Vanilla Developing

Backend

```bash
$ ./gradlew quarkusDev
```
Make your DNS queries to IP and TCP/UDP ports indicated at the console log.

Front end app (optional)

```bash
$ cd app && npm start
```

Then access http://localhost:3000/ , front end will proxy to http://localhost:5380 backend.

## With Docker

```bash
$ ./gradlew build -Dquarkus.package.type=uber-jar -i -x check
$ docker-compose -f docker-compose-dev.yml up
```

or 

```bash
$ ./gradlew build -Dquarkus.package.type=uber-jar -i -x check
$ docker-compose -f docker-compose-dev.yml run --rm -it backend bash
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
