---
title: Release Process
weight: 7
pre: "<b>7. </b>"
---

## Feature Request - Phase 1

* Create a pull request
* CI will be checked automatically
* Once CI is passing, the author `@mageddo` must approve the PR
* Merge the PR

## Pre-Releasing New Version - Phase 2

* Define the new version number
* Document the new version and changes at `RELEASE-NOTES.md`
* Generate the git tag with the steps below

Patch Version
```bash
$ ./gradlew release
```

Major Version
```bash
$ VERSION=3.18.0 && ./gradlew release -Prelease.releaseVersion=${VERSION} -Prelease.newVersion=${VERSION}
```

## Release Latest - Phase 3

* Define a pre-release version which will be promoted to the latest by [following the rules][1].
* Edit the github release setting the pre-release version as the latest, remove the `-snapshot` suffix from the title 
and from the attachments names.
* Locally tag the latest docker image as the pre-release version
```bash
$ docker tag defreitas/dns-proxy-server:${PRE_RELEASE_VERSION} mageddo/dns-proxy-server:latest
```
* Check the docker image version by 
```bash
$ docker run defreitas/dns-proxy-server:latest --version
```
* Push the pre-release version to the docker hub
```bash
$ docker push defreitas/dns-proxy-server:latest
```

[1]: {{%relref "1-getting-started/versioning.en.md" %}}
