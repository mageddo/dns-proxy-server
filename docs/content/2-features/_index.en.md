---
title: Features
weight: 2
pre: "<b>2. </b>"
---
## Features

{{%children style="li"  %}}
* IPV4/IPV6 Support 

## Main features use cases

Solving container hostname
```bash
$ nslookup dns.mageddo
Server:         127.0.0.53
Address:        127.0.0.53#53

Non-authoritative answer:
Name:   dns.mageddo
Address: 172.157.5.1
```

Solving host machine IP
```bash
$ nslookup host.docker
Server:         127.0.0.53
Address:        127.0.0.53#53

Non-authoritative answer:
Name:   host.docker
Address: 172.157.5.1
```

Solving from Internet
```bash
$ nslookup acme.com
Server:         127.0.0.53
Address:        127.0.0.53#53

Non-authoritative answer:
Name:   acme.com
Address: 23.93.76.124
```

Solving from local database
```bash
$ nslookup dps-sample.dev
Server:         127.0.0.53
Address:        127.0.0.53#53

Non-authoritative answer:
Name:   dps-sample.dev
Address: 192.168.0.254
```

Access the gui by using http://localhost:5380 , see [this link][1].

## DNS resolution order
**DPS** follow the below order to solve hostnames

* Try to solve the hostname from **docker** containers
* Then from local database file
* Then from 3rd configured remote DNS servers

[1]: {{%relref "2-features/gui/_index.md" %}}
