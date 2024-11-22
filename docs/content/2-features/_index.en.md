---
title: Features
weight: 2
pre: "<b>2. </b>"
---

## DNS Features List

* [🟡 Authoritative][1]
* [✅ Recursive][2]
* [❌ Recursion ACL][3]
* ✅ Secondary mode
* [✅ Caching][4]
* [✅ IPv6][7]
* [✅ Wildcard][8]
* [❌ Split horizon][9]
* [❌ DNS over HTTPS][10]
* [❌ DNS over QUIC][11]
* [❓DNSSEC][5]
* [❓TSIG][6]

```
❓ = Unknown
❌ = Not implemented
✅ = Implemented
🟡 = Partially Implemented
```

## Other Features Manual

{{%children style="li"  %}}

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

Access the gui by using http://localhost:5380 , see [this link][12].

## DNS resolution order
**DPS** follow the below order to solve hostnames

* Try to solve the hostname from **docker** containers
* Then from local database file
* Then from 3rd configured remote DNS servers


[1]: https://en.wikipedia.org/wiki/Name_server#Authoritative_name_server
[2]: https://en.wikipedia.org/wiki/Name_server#Recursive_query
[3]: https://en.wikipedia.org/wiki/Access_control_list
[4]: https://en.wikipedia.org/wiki/Name_server#Caching_name_server
[5]: https://en.wikipedia.org/wiki/Domain_Name_System_Security_Extensions
[6]: https://en.wikipedia.org/wiki/TSIG
[7]: https://en.wikipedia.org/wiki/IPv6
[8]: https://en.wikipedia.org/wiki/Wildcard_DNS_record
[9]: https://en.wikipedia.org/wiki/Split-horizon_DNS
[10]: https://en.wikipedia.org/wiki/DNS_over_HTTPS
[11]: https://en.wikipedia.org/wiki/DNS_over_QUIC
[12]: {{%relref "2-features/gui/_index.md" %}}
