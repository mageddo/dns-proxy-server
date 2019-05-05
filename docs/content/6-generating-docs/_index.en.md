---
title: Generating the docs
weight: 6
pre: "<b>5. </b>"
---

DPS uses Hugo to generate static docs. To generate the HTML use the following

```bash
$ 
VERSION=cat VERSION | awk -F '.' '{ print $1"."$2}' &&\
hugo server --appendPort=false --renderToDisk \
--source docs/ --destination $PWD/../dns-proxy-server-docs/${VERSION}/ \
--baseURL=http://mageddo.github.io/dns-proxy-server/${VERSION}
```

