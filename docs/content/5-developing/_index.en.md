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

