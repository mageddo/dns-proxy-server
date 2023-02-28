[![CI](https://github.com/mageddo/dns-proxy-server/actions/workflows/ci.yml/badge.svg)](https://github.com/mageddo/dns-proxy-server/actions/workflows/ci.yml)
[![help me to keep DPS up to date][7]][6]

### Main features

DPS is a lightweight end user (Developers, Server Administrators) DNS server tool 
which make it easy to develop in systems where one hostname can solve to different IPs based 
on the configured environment, so you can:

* Solve hostnames from local configuration database
* Solve hostnames from docker containers using docker **hostname** option or **HOSTNAMES** env
* Solve hostnames from a list of configured remote DNS servers(as a proxy) if no answer of two above
* Solve hostnames using wildcards
* Graphic interface to Create/List/Update/Delete **A/CNAME** records
* Solve host machine IP using `host.docker` hostname
* Access container by its container name / service name
* Specify from which network solve container IP 

Checkout the [full list of features][4] with examples

![](https://i.imgur.com/aR9dl0O.png)

### Basic running it 

You can run DPS as native binary downloading the latest [binaries releases][2] 
or via docker looking at [Dockerhub images][3].

Basic running it on Linux
```bash
$ curl -s -L https://github.com/mageddo/dns-proxy-server/releases/download/3.5.0/dns-proxy-server-linux-amd64-3.5.0.tgz | tar -vzx &&\
sudo ./dns-proxy-server
```

Then you can solve pre-configured entries (conf/config.json): 
```bash
$ ping dps-sample.dev
.......
```

Also solve Docker containers:
```bash
$ docker run --rm --hostname nginx.dev nginx
$ ping nginx.dev
PING nginx.dev (172.17.0.4) 56(84) bytes of data.
64 bytes from 172.17.0.4: icmp_seq=1 ttl=64 time=0.063 ms
64 bytes from 172.17.0.4: icmp_seq=2 ttl=64 time=0.074 ms
64 bytes from 172.17.0.4: icmp_seq=3 ttl=64 time=0.064 ms
```

See [complete running it][5] documentation for more use cases, like running on Docker, Windows, etc.

### Documents
* [Full documentation](http://mageddo.github.io/dns-proxy-server/)
* [Running it documentation][5]
* [Examples](https://github.com/mageddo/dns-proxy-server/tree/master/examples)
* [Coding at the DPS](http://mageddo.github.io/dns-proxy-server/latest/en/5-developing/)
* [RFC-1035][1]

### Donation
Help me to keep DPS up to date

Via PayPal

[![][7]][6]

Or via QR code

![](https://i.imgur.com/LmN7g2j.png)

[1]: https://www.ietf.org/rfc/rfc1035.txt 
[2]: https://github.com/mageddo/dns-proxy-server/releases
[3]: https://hub.docker.com/r/defreitas/dns-proxy-server
[4]: http://mageddo.github.io/dns-proxy-server/latest/en/2-features/
[5]: http://mageddo.github.io/dns-proxy-server/latest/en/1-getting-started/running-it/
[6]: https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PYFAZCXL442B6&source=url
[7]: https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif
