---
title: Configuring a Service Discovery solution on Docker using DNS Proxy Server and NGINX
---

Tested on: 

* DPS 3.11
* OS: Mac, Linux


You will see how to develop with docker containers solving them by hostnames from your host machine
as the following picture shows:

![](https://i.imgur.com/wr9GSeR.png) 

if you know about DPS you will figure out that's a very basic DPS feature and you don't need complex configurations to
achieve that feature except on MAC and Windows as docker runs its containers on a virtual machine on Mac and Windows
them DPS default feature to solve containers names will work but the solved IPs are worthless because they aren't 
accessible from the host.

So we will configure an API Gateway, Service Discovery, Reverse Proxy solution here combining DPS with Nginx then 
finally fixing this limitation, see final solution below:

> Obs: This tutorial won't work on Windows yet because containers solving is not support yet,
> follow the [feature request issue][1] on github.

![][2]
Source: [excalidraw][3]

The pratice is simpler than the teory, let's get it working: 

```bash
$ git clone https://github.com/mageddo/dns-proxy-server.git
$ cd examples/api-gateway_service-discovery_reverse-proxyame-compose-file
$ docker-compose up --build
```

```bash
$ curl -i -X GET http://web-app.webapp
HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 16 Mar 2023 03:18:53 GMT
Content-Type: text/html
Content-Length: 37
Connection: keep-alive
Last-Modified: Thu, 16 Mar 2023 02:35:50 GMT
ETag: "64128086-25"
Accept-Ranges: bytes

<h1>Hello World from web-app!!!</h1>
```

> Service Discovery, API Gateway, Service Discovery, Reverse Proxy

[1]: https://github.com/mageddo/dns-proxy-server/issues/314
[2]: https://i.imgur.com/poI0sKZ.png
[3]: https://excalidraw.com/#json=BuYYx179GhmvHCexDZHGv,2hN_IgZo9HTfID-neSACQw
