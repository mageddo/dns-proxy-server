---
title: DNS Over HTTPS
---

DPS supports [DNS over HTTPS][2].
When using DPS, the main benefit is that you can configure the DNS server directly in the browser, so you don’t need to change the system’s default DNS to access hostnames in your browser.

### Enabling
Set `server.doh.port` for a free port, then doH will be enabled. See the [configs reference][1] for details.

```bash
$ docker run --rm -p 8443:8443 -e DPS_SERVER__DOH__PORT=8443 defreitas/dns-proxy-server:5.8.2-snapshot
```

```bash
$ curl -k https://localhost:8443/health
ok
```

### Using DoH on the Browser
* Startup DPS with DoH enabled
* Import DPS auto assigned certificate authority
* Configure DPS as the Browser DoH 
* Disable [RFC-1918][3] restrictions on the Browser
* You are done!

Configuring browsers

{{%children style="li"  %}}


[1]: {{%relref "3-configuration/_index.md" %}}#doh-server
[2]: https://en.wikipedia.org/wiki/DNS_over_HTTPS
[3]: https://datatracker.ietf.org/doc/html/rfc1918
[4]: https://raw.githubusercontent.com/mageddo/dns-proxy-server/607af35d2fc985a8ad9b6cb4b7953f6e87335d97/doh/ca.crt
