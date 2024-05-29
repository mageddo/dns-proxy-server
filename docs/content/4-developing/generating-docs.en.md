---
title: Generating the docs
weight: 6
pre: "<b>6. </b>"
---

### Requirements
* Hugo 0.55.4

### Live Docs Preview

#### Vanilla

```bash
$ hugo server --source docs
```

#### Docker

```bash
$ docker-compose -f docker-compose-dev.yml up docs
```

### Generating Doc Statics

DPS uses Hugo to generate static docs. To generate the HTML use the following

```bash
$ ./builder.bash docs
```
