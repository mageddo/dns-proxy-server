<a href="https://travis-ci.org/mageddo/dns-proxy-server"><img src="https://travis-ci.org/mageddo/dns-proxy-server.svg?branch=master" alt="Build Status" /></a>&nbsp;&nbsp;
[![help me to keep DPS up to date](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PYFAZCXL442B6&source=url)

### Main features

DPS is a end user(developers, Server Administrators) DNS server tool to develop systems with docker solving
docker containers hostnames:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured DNS servers(as a proxy) if no answer of two above
* Solve hostnames using wildcards
* Graphic interface to Create/List/Update/Delete **A/CNAME** records
* Solve host machine IP using `host.docker` hostname
* Access container by it’s container name / service name
* Specify from which network solve container IP 

![](https://i.imgur.com/aR9dl0O.png)

### Basic running it

```bash
$ docker run --rm --hostname dns.mageddo \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /etc/resolv.conf:/etc/resolv.conf \
defreitas/dns-proxy-server
```

then try it out

```bash
$ ping dns.mageddo
PING dns.mageddo (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4: icmp_seq=1 ttl=64 time=0.063 ms
64 bytes from 172.17.0.4: icmp_seq=2 ttl=64 time=0.074 ms
64 bytes from 172.17.0.4: icmp_seq=3 ttl=64 time=0.064 ms
```

### Documents
* [Full documentation](http://mageddo.github.io/dns-proxy-server/)
* [Running it documentation](http://mageddo.github.io/dns-proxy-server/latest/en/1-getting-started/running-it/)
* [Examples](https://github.com/mageddo/dns-proxy-server/tree/master/examples)
* [Coding at the DPS](http://mageddo.github.io/dns-proxy-server/en/5-developing/)

### Donation
Help me to keep DPS up to date

Via PayPal

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PYFAZCXL442B6&source=url)

Or via QR code

![](https://i.imgur.com/LmN7g2j.png)

