---
title: Stub Solver
---

You can use stub solver to transform IPs into hostnames without need to create a 
configuration file as in Local DB Solver. Stub Solver was inspired in `nip.io` and `sslip.io`.

## Examples:

**Without a name:**

* `10.0.0.1.stub` => `10.0.0.1`
* `192-168-1-250.stub` => `192.168.1.250`
* `0a000803.stub` => `10.0.8.3`

**With a name:**

* `app.10.8.0.1.stub` => `10.8.0.1`
* `app-116-203-255-68.stub` => `116.203.255.68`
* `app-c0a801fc.stub` => `192.168.1.252`
* `customer1.app.10.0.0.1.stub` => `10.0.0.1`
* `customer2-app-127-0-0-1.stub` => `127.0.0.1`
* `customer3-app-7f000101.stub` => `127.0.1.1`
* `app.2a01-4f8-c17-b8f--2.stub` => `2a01:4f8:c17:b8f::2`

### Format Reference

```
${name}[.-]${ip_address}.stub
```

* `${name}` (optional): you can set a name to make it easier to recognize the IP
* `${ip_address}`: The ip address which the DNS will answer it can be in 4 formats
  * dot notation: magic.127.0.0.1.stub
  * dash notation: magic-127-0-0-1.stub
  * hexadecimal notation: magic-7f000001.stub
  * Ipv6 notation: magic.2a01-4f8-c17-b8f--2.stub

### Customize the domain name

&#x3C;tbd&#x3E;

## Refs

* [Stub Solver #545][1]

 
[1]: https://github.com/mageddo/dns-proxy-server/issues/545
