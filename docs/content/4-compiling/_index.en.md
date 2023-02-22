---
title: Compiling from source
weight: 4
pre: "<b>4. </b>"
---

### Requirements
* JDK 17+

### Building from source

Build the frontend files (optional)

```bash
./builder.bash build-frontend
```

Build and run the program
```bash

$ ./gradlew build -Dquarkus.package.type=uber-jar && java -jar ./build/dns-proxy-server.jar
```

