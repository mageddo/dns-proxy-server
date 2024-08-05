---
title: Compiling from source
weight: 4
pre: "<b>1. </b>"
---

## Requirements
See the [requirements][1].

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

### Native Image Test

Tests ending with `IntTest.java` can be run within the native image binary to check if 
the native-image compilation produces a working binary version.

```
$ ./gradlew clean nativeIntTest
```

[1]: {{%relref "1-getting-started/requirements/_index.en.md" %}}
