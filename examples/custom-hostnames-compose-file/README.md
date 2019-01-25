If you wanna register many different hostnames to the same container or solve containers from host then you can follow this example

```bash
$ Every 2s: curl -s -I nginx.server                           2019-01-25 05:46:07
curl-client_1_73be594e986d | 
curl-client_1_73be594e986d | HTTP/1.1 200 OK
curl-client_1_73be594e986d | Server: nginx/1.15.8
curl-client_1_73be594e986d | Date: Fri, 25 Jan 2019 05:46:07 GMT
curl-client_1_73be594e986d | Content-Type: text/html
curl-client_1_73be594e986d | Content-Length: 612
curl-client_1_73be594e986d | Last-Modified: Tue, 25 Dec 2018 09:56:47 GMT
curl-client_1_73be594e986d | Connection: keep-alive
curl-client_1_73be594e986d | ETag: "5c21fedf-264"
curl-client_1_73be594e986d | Accept-Ranges: bytes
curl-client_1_73be594e986d | 
nginx_1_1edd2e86b950 | 172.0.0.3 - - [25/Jan/2019:05:46:07 +0000] "HEAD / HTTP/1.1" 200 0 "-" "curl/7.61.1" "-" 
```

Docker version

```
docker version
Client: Docker Engine - Community
 Version:           18.09.0
 API version:       1.39
 Go version:        go1.10.4
 Git commit:        4d60db4
 Built:             Wed Nov  7 00:46:51 2018
 OS/Arch:           linux/amd64
 Experimental:      false

Server: Docker Engine - Community
 Engine:
  Version:          18.09.0
  API version:      1.39 (minimum version 1.12)
  Go version:       go1.10.4
  Git commit:       4d60db4
  Built:            Wed Nov  7 00:52:55 2018
  OS/Arch:          linux/amd64
  Experimental:     false

```
