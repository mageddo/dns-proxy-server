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

Generated possible necessary metadata
```bash
./gradlew clean -Pagent compTest
./gradlew metadataCopy
```

Generate the binary
```bash
$ ./gradlew -x check clean build nativeIntTest nativeImageJar nativeCompile
$ ./build/native/nativeCompile/dns-proxy-server
```

### AMD64 Static

Compiling the source JAR
```bash
$ ./gradlew build -x check shadowJar nativeImageJar
$ mkdir -p build/artifacts/native-image-source && cp build/libs/native-image-*.jar ./build/artifacts/native-image-source/dns-proxy-server.jar
$ tree build/artifacts/
build/artifacts/
└── native-image-source
    └── dns-proxy-server.jar

1 directory, 1 file
```

Generating the native image

```bash
$ ./builder.bash build-backend amd64-static
```

[1]: {{%relref "1-getting-started/requirements/_index.en.md" %}}
