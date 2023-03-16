---
title: Configuring a Service Discovery solution on Docker using DNS Proxy Server and NGINX
menuTitle: Create a Service Discovery solution
---

##### Requirements
* DPS already configured, [see the docs][4] if you haven't yet.

##### Tested on: 

* DPS 3.11
* OS: Mac, Linux

#### Introducing

You will see how to develop with docker containers solving them by hostnames from your host machine
as the following picture shows:

![](https://i.imgur.com/wr9GSeR.png) 

if you know about DPS you will figure out that's a very basic DPS feature and you don't need complex configurations to
achieve that, except on MAC and Windows as docker runs its containers on a virtual machine so
DPS default feature to solve containers names will work but the solved IPs are worthless because they aren't 
accessible from the host.

To fix that we will configure an API Gateway, Service Discovery, Reverse Proxy solution combining DPS with Nginx,
see final solution below:

> Obs: This tutorial won't work on Windows yet because containers solving is not support yet,
> follow the [feature request issue][1] on github.

![][2]
Source: [excalidraw][3]

### Configuring 
The pratice is simpler than the theory, let's get it working: 

Configuring Nginx reverse proxy and two web apps for test
```bash
$ git clone https://github.com/mageddo/dns-proxy-server.git
$ cd examples/api-gateway_service-discovery_reverse-proxyame-compose-file
$ docker-compose up --build
```

`docker-compose up` created three containers, the first is a nginx reverse proxy which will listen to the host machine
80 port and two others are web apps which the reverse proxy will proxy to depending on the hostname queried.

Reverse Proxy will listen all `.webapp` hostnames and proxy to `.container` containers.  
See [docker-compose.yml][5] for more details about each docker service. 

The final step is to configure a wildcard to handle when something on the host queries for `.webapp`:

![][6]

### Testing

#### From the hostname

Web app 1 is working:
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

Web app 2 is also working:
```bash
$ curl -i -X GET http://web-app-2.webapp
HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 16 Mar 2023 03:30:19 GMT
Content-Type: text/html
Content-Length: 615
Connection: keep-alive
Last-Modified: Tue, 13 Dec 2022 15:53:53 GMT
ETag: "6398a011-267"
Accept-Ranges: bytes

<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
...
```

#### Apps can also access each other by its hostname
No reverse proxy is necessary here, so you can connect a web-app to a database container by its hostname for example.

web-app-1 connecting to web-app-2
```bash
$ docker-compose run web-app curl -I web-app-2.webapp.container
HTTP/1.1 200 OK
Server: nginx/1.23.3
Date: Thu, 16 Mar 2023 03:55:02 GMT
Content-Type: text/html
Content-Length: 615
Last-Modified: Tue, 13 Dec 2022 15:53:53 GMT
Connection: keep-alive
```


That's all, if you have any contribution or question feel free to open a pull request or an issue on the Github repo,
thanks for reading.

> Service Discovery, API Gateway, Service Discovery, Reverse Proxy

[1]: https://github.com/mageddo/dns-proxy-server/issues/314
[2]: https://i.imgur.com/poI0sKZ.png
[3]: https://excalidraw.com/#json=BuYYx179GhmvHCexDZHGv,2hN_IgZo9HTfID-neSACQw
[4]: {{%relref "1-getting-started/running-it/_index.md" %}}
[5]: https://github.com/mageddo/dns-proxy-server/blob/master/examples/api-gateway_service-discovery_reverse-proxy/docker-compose.yml
[6]: https://i.imgur.com/xRrk2Mk.png
