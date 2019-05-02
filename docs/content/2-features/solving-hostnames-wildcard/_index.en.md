---
title: Solving hostnames using wildcards
weight: 3
---

If you register a hostname with `.` at start, then all subdomains will solve to that container/local storage entry

Example

	docker run --rm --hostname .mageddo.com nginx:latest

Now all **mageddo.com** subdomains will solve to that nginx container

```
$ nslookup site1.mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	site1.mageddo.com
Address: 172.17.0.5

$ nslookup mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	mageddo.com
Address: 172.17.0.5

```
