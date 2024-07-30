---
title: Generating the docs
weight: 6
pre: "<b>2. </b>"
---

## Introduction

DPS uses Hugo and the theme-learn to generate static docs.


## Requirements
* Hugo 0.55.4

## Live Docs Preview

### Vanilla

```bash
$ hugo server --source docs
```

### Docker

```bash
$ docker-compose -f docker-compose-dev.yml up docs
```

## Generating Doc Statics

To generate the HTML use the following

```bash
$ ./builder.bash docs
```
