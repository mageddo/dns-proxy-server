Even when the containers have different networks, DPS will work, [see the docs][1] for more details.

```bash
$ curl -I nginx.app
HTTP/1.1 200 OK
Server: nginx/1.22.1
```

[1]: http://mageddo.github.io/dns-proxy-server/latest/en/2-features/docker-different-networks-solving
