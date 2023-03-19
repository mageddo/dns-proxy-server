---
title: Wildcards hostnames Solving
weight: 3
---

## Wildcard

If you register a hostname with `.` at start, then all subdomains will solve to that container/local storage entry

Example

```
$ docker run --rm --hostname .mageddo.com nginx:latest
```

Now all **mageddo.com** subdomains will solve to that nginx container

```
$ nslookup site1.mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	site1.mageddo.com
Address: 172.17.0.5
```

```
$ nslookup mageddo.com
Server:		172.17.0.4
Address:	172.17.0.4#53

Non-authoritative answer:
Name:	mageddo.com
Address: 172.17.0.5
```

## RegEx

From DPS `3.14` regex is also supported on localstorage or docker container hostname/HOSTNAMES env,
the pattern is `/${REGEX}/`, everything around the slashes is considered as regex

**Example:**

Running a sample container
```bash
$ docker run --rm --name nginx1 --hostname '/batata\.[a-z]+\.com/' -e 'HOSTNAMES=/\d+\.acme\.com/,/sub\.acme\.com/' nginx
```

Solving
```
$ nslookup batata.whatever.com
Server:		127.0.0.1
Address:	127.0.0.1#53

Non-authoritative answer:
Name:	batata.whatever.com
Address: 172.17.0.4
```

```
$ nslookup xpto.acme.com
Server:		127.0.0.1
Address:	127.0.0.1#53

** server can't find xpto.acme.com: NXDOMAIN
```

```
$ nslookup 123.acme.com
Server:		127.0.0.1
Address:	127.0.0.1#53

Non-authoritative answer:
Name:	123.acme.com
Address: 172.17.0.4
```

```
$ nslookup sub.acme.com
Server:		127.0.0.1
Address:	127.0.0.1#53

Non-authoritative answer:
Name:	sub.acme.com
Address: 172.17.0.4
```
