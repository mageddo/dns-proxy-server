---
title: Features
weight: 2
pre: "<b>2. </b>"
---
### Features

{{%children style="li"  %}}

### DNS resolution order
**DPS** follow the below order to solve hostnames

* Try to solve the hostname from **docker** containers
* Then from local database file
* Then from 3rd configured remote DNS servers

