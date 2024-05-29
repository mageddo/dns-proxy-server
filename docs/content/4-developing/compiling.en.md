---
title: Compiling from source
weight: 4
pre: "<b>4. </b>"
---

## Requirements

See {{%relref "1-getting-started/requirements/_index.en.md" %}}.

## Building from Source

Build the frontend files (optional)

```bash
./builder.bash build-frontend
```

Build and run the program

#### Jar file
```bash
$ ./gradlew clean build compTest shadowJar -i
$ java -jar dns-proxy-server-*-all.jar
```

### Native Image

```shell
$ ./gradlew clean build compTest shadowJar nativeCompile
$ ./build/native/nativeCompile/dns-proxy-server
```
