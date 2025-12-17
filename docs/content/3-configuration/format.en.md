---
title: Supported Formats
weight: 2
pre: "<b>2. </b>"
---

## Supported Formats
* ENV
* YAML
* JSON
* Command Line Arguments

### YAML/JSON File Path Configuration
**Default**: `conf/config.json`

The location can be changed by setting `DPS_CONFIG_FILE_PATH`, `MG_CONFIG_FILE_PATH` legacy env or
`--conf-path` command line argument. The path can be relative (to the binary path) or absolute.

### Working Dir
**Default**: DPS executing path, a.k.a Working Directory

Is the path which will be used when **ConfigFilePath** is set as a relative path.
Can be set by `DPS_WORK_DIR` or `DPS_WORK_DIR` legacy env.

### Environment Variable configuration
Environment Variable configuration are dynamically generated respecting the file configuration using the format:

* `DPS_${property}`
* `DPS_${property}__${subProperty}`, ex: `DPS_SERVER__PROTOCOL`
* `DPS_${property}_${index}__${property}`, ex: `DPS_SOLVER__LOCAL__ENVS_0__HOSTNAMES_0__TARGET`

**Boolean values**

You can use `1` or `true` (case-insensitive) to specify which the flag is activated, any other
value will be considered false.

### Terminal configuration
Run one of the commands below to get the commandline instructions help:

```bash
$ ./dns-proxy-server --help
```

```bash
$ docker run defreitas/dns-proxy-server --help
```
