---
title: Developing
weight: 5
pre: "<b>5. </b>"
---

## Requirements 
See the [requirements][1].

## Vanilla Developing

### Backend

Play class `com.mageddo.dnsproxyserver.App` or run

```bash
$  ./gradlew clean shadowJar && java -jar ./build/libs/dns-proxy-server-*-all.jar 
```

Make your DNS queries to IP and TCP/UDP ports indicated at the console log.

### Front end app (optional)

```bash
$ cd app && npm start
```

Then access http://localhost:3000/ , front end will proxy to http://localhost:5380 backend.

## Docker Developing 

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

## Automated Tests

### Unit Tests

Verify the functionality of individual components or units of code, typically functions or methods, 
to ensure they work as expected.

### Comp Tests

Verify the functionality of entire flows but can mock some parts, example is an endpoint
which changes some setting at config file, etc.

### Int Tests

Are like Comp Test but used to test native-image compiling, for that reason they
can't mock using frameworks like Mockito, this kind of test need to evict mocks and when do,
use flags instead of stubs/mocks.

Tests ending with `IntTest.java` can be run within the native image binary to check if
the native-image compilation produces a working binary version.

Run Int Tests within the Native Image

```bash
$ ./gradlew clean nativeIntTest
```

Run Int Tests within the jar version

```bash
$ ./gradlew clean intTest
```

[1]: {{%relref "1-getting-started/requirements/_index.en.md" %}}
