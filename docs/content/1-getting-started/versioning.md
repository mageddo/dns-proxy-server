---
title: Release Version Control
weight: 3
---

## DPS Versioning

DPS uses [semantic versioning][1] \[[2][6]\] for releasing. The latest stable version is the one which is marked
as "latest" label at Github releases or the latest docker tag. [Click here][2] to see the binary releases at Github.

## Promotion to the latest (Stable)
Minor pre-releases with at least 1 month since they released without a confirmed bug issued on the Github oficial repo,
will be released as the latest. A pre-release can take more than 1 month to be promoted to the latest though, if 
you want to use the most recent updates check the latest pre-release or the docker `nightly` tag.

## Docker Images

There are three types of docker images

* Latest images are the latest stable image for Linux amd64
* Nightly/Unstable images are the latest **un**stable image for Linux amd64
* Images for Linux amd64: ex: `defreitas/dns-proxy-server:3.9.0`
* Images for Linux aarch64: ex: `defreitas/dns-proxy-server:3.9.0-aarch64`

[Click here][3] to see the docker images at docker hub.

## Release Notes
You can see every release note on the [releases download page][2] or at the [release notes.md][5] file

## Reference
DPS release process was inspired on [Gradle][4] and Debian releasing process.

[1]: https://en.wikipedia.org/wiki/Software_versioning#Semantic_versioning
[2]: https://github.com/mageddo/dns-proxy-server/releases
[3]: https://hub.docker.com/r/defreitas/dns-proxy-server/
[4]: https://docs.gradle.org/7.6.1/release-notes.html
[5]: https://github.com/mageddo/dns-proxy-server/blob/master/RELEASE-NOTES.md
[6]: https://semver.org/
