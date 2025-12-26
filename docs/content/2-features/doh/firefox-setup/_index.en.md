---
title: Configure DPS DoH on Firefox
---

## Step by Step for Firefox

### Import DPS auto assigned certificate authority
* Access `about:preferences#privacy`
* Scroll Down to `Certificates`
* Click on `View Certificates`
* Go to `Authorities` Tab
* Click on `Import...`, import [ca.crt][4] file, click on checkboxes to trust and confirm.

### Configure DPS as the Browser DoH
* Access `about:preferences#privacy`
* Scroll down to `DNS over HTTPS`
* Tick `Increased Protection` radio button
* Choose `Custom` on `Custom Provider` Combo Box
* Put `https://localhost:8443/dns-query` on the input which appear below
* `Status: Active  Provider: localhost` must appear below `DNS over HTTPS` title

### Disable RFC-1918 restrictions on the Browser
We need to disable RFC-1918 on the browser to make browser able to accept private IPs for hostnames solved on DoH server.
The [RFC-1918][3] defines what are private, public IPs, when and where they should be used. For this reason, Browsers
won't accept to resolve private IPs from hostnames solved using DoH because it probably is not a production usecase,
the environment which DoH was thought to be used.

* Access `about:config`
* Find `network.trr.allow-rfc1918` and change it to **true**

## Additional Considerations
In my tests, some real domains like `.dev` won't work depending on the combination
of private ip + default port (80, 443), the browser will not accept to solve, so evict them, **.com** seems to work
normally;

You can track which names are being solved by accessing `about:networking#dns`

[2]: https://en.wikipedia.org/wiki/DNS_over_HTTPS
[3]: https://datatracker.ietf.org/doc/html/rfc1918
[4]: https://raw.githubusercontent.com/mageddo/dns-proxy-server/607af35d2fc985a8ad9b6cb4b7953f6e87335d97/doh/ca.crt
