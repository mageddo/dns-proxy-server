Note if you are using docker-compose v2 or v3 DPS is not necessary to solve containers on the same compose file, note 
you will not be able to solve containers from host

```bash
$ docker-compose -f docker-compose-v2.yml up --force-recreate
Every 2s: curl -s -I nginx                                  2019-01-25 05:03:09
curl-client_1_b51dbdc408d6 | 
nginx_1_d21a8dc439bc | 172.25.0.2 - - [25/Jan/2019:05:03:09 +0000] "HEAD / HTTP/1.1" 200 0 "-" "curl/7.61.1" "-"
curl-client_1_b51dbdc408d6 | HTTP/1.1 200 OK
curl-client_1_b51dbdc408d6 | Server: nginx/1.15.8
curl-client_1_b51dbdc408d6 | Date: Fri, 25 Jan 2019 05:03:09 GMT
curl-client_1_b51dbdc408d6 | Content-Type: text/html
curl-client_1_b51dbdc408d6 | Content-Length: 612
curl-client_1_b51dbdc408d6 | Last-Modified: Tue, 25 Dec 2018 09:56:47 GMT
curl-client_1_b51dbdc408d6 | Connection: keep-alive
curl-client_1_b51dbdc408d6 | ETag: "5c21fedf-264"
curl-client_1_b51dbdc408d6 | Accept-Ranges: bytes 
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
