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

Front end app (optional)

```bash
$ cd app && npm start
```

### Developing with docker

	$ docker-compose rm -f && docker-compose up --build app-dps compiler-dps

Running the application 

```
$ docker-compose exec compiler-dps bash
$ go run dns.go
```

Running the GUI

```
$ docker-compose exec app-dps sh
$ npm start
```

Running unit tests

	$ go test -cover=false ./.../


