---
title: Release Process
weight: 7
pre: "<b>3. </b>"
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
* Edit the github release setting latest flag
* Run [the release latest][2] workflow, if you don't run the workflow it will auto run on the next day at 07:30 UTC 

[1]: {{%relref "1-getting-started/versioning.en.md" %}}
[2]: https://github.com/mageddo/dns-proxy-server/actions/workflows/release-latest.yml
