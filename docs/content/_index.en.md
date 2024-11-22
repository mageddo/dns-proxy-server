[![CI](https://github.com/mageddo/dns-proxy-server/actions/workflows/ci.yml/badge.svg)](https://github.com/mageddo/dns-proxy-server/actions/workflows/ci.yml)
[![help me to keep DPS up to date][7]][6]

### Main features

DPS is a lightweight end user (Developers, Server Administrators) DNS server tool
which make it easy to develop in systems where one hostname can solve to different IPs based
on the configured environment, so you can:

* Solve hostnames from local configuration database
* Solve hostnames from [docker containers][11]
* Solve hostnames from a list of configured remote DNS servers(as a proxy) if no answer of two above
* Solve hostnames using wildcards
* Graphic interface to Create/List/Update/Delete **A/CNAME** records
* Solve host machine IP using `host.docker` hostname


Checkout the [full list of features][4] with examples

![](https://i.imgur.com/aR9dl0O.png)

### Basic running it

You can run DPS as native binary downloading the latest [binaries releases][2]
or via docker looking at [Dockerhub images][3]. See [complete running it][5] documentation for running on
Mac, Windows, Docker, etc.

Basic running it on Linux or Mac

```bash
sudo ./dns-proxy-server --server-port 5555 --register-container-names
```

Solving docker container [from container name][11]
```bash
$ docker run --rm --name nginx-app nginx

$ nslookup -po=5555 nginx-app.docker 127.0.0.1
172.17.0.3
```

Solving docker container from hostname
```bash
$ docker run --rm --hostname nginx.dev nginx

$ nslookup -po=5555 nginx.dev 127.0.0.1
172.17.0.3
```

Solving from pre-configured entries (conf/config.json):
```bash
$ nslookup -po=5555 dps-sample.dev 127.0.0.1
192.168.0.254
```

Solving from Internet
```bash
$ nslookup -po=5555 google.com 127.0.0.1
142.250.79.174
```

Solving stub hostnames like nip.io or sslip.io
```bash
$ nslookup -po=5555 machine-1.192.168.0.1.stub 127.0.0.1
192.168.0.1
```

Solving host machine IP
```bash
$ nslookup -po=5555 host.docker 127.0.0.1
172.22.230.67
```

Solving all subdomains to a specific docker container

```bash
$ docker run --rm --hostname .nginx.dev nginx

$ nslookup -po=5555 site1.nginx.dev 127.0.0.1
172.17.0.3
```

Check more [samples][9] to learn by practice.

### Documents
* [Full documentation](http://mageddo.github.io/dns-proxy-server/)
* [Running it documentation][5]
* [Tutorials and Examples][9]
* [Coding at the DPS][10]
* [RFC-1035][1]

### Versioning and Releasing
Please be aware of [how DPS controls the releases][8] so you can use the most recent features and updates or
prefer to choose the more stable and old builds.

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
[8]: http://mageddo.github.io/dns-proxy-server/latest/en/1-getting-started/versioning
[9]: http://mageddo.github.io/dns-proxy-server/latest/en/5-tutorials/
[10]: http://mageddo.github.io/dns-proxy-server/latest/en/4-developing/
[11]: https://mageddo.github.io/dns-proxy-server/latest/en/2-features/docker-solving/
