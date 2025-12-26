---
title: Configure DPS DoH on Chrome (Not confirmed)
---

### Import DPS auto assigned certificate authority

Chrome relies on the **operating system trust store**, so the DPS CA must be imported at the OS level.

**Linux (Ubuntu / Debian-based):**

* Copy the [CA file][4] to the system certificates directory

  ```bash
  sudo cp ca.crt /usr/local/share/ca-certificates/dps-ca.crt
  ```
* Update the system trust store

  ```bash
  sudo update-ca-certificates
  ```
* Restart Google Chrome

**macOS:**

* Open **Keychain Access**
* Select **System** keychain
* Drag and drop `ca.crt` into the certificates list
* Double-click the imported certificate
* Expand **Trust**
* Set **When using this certificate** to **Always Trust**
* Close the window and authenticate
* Restart Google Chrome

**Windows:**

* Double-click `ca.crt`
* Click **Install Certificate**
* Choose **Local Machine**
* Select **Place all certificates in the following store**
* Choose **Trusted Root Certification Authorities**
* Finish the wizard
* Restart Google Chrome

---

### Configure DPS as the Browser DoH

* Access `chrome://settings/security`
* Scroll down to `Advanced`
* Find `Use secure DNS`
* Enable the `Use secure DNS` toggle
* Select `With Custom`
* Put `https://localhost:8443/dns-query` in the provider input
* The secure DNS section must indicate that a **custom provider is in use**

---

### Disable RFC-1918 restrictions on the Browser

We need to disable RFC-1918 restrictions on the browser to make it able to accept private IPs for hostnames resolved via DoH.
The [RFC-1918][3] defines what are private and public IPs, and browsers restrict their use in DoH responses because this is
not considered a typical production use case.

Chrome blocks private IP resolution via DoH by default as a security measure.

* Access `chrome://flags`
* Search for `Insecure Private Network Requests`
* Set **Block insecure private network requests** to **Disabled**
* Restart Google Chrome

## Additional Considerations
In my tests, some real domains like `.dev` won't work depending on the combination
of private ip + default port (80, 443), the browser will not accept to solve, so evict them, **.com** seems to work
normally;

You can track which names are being solved by accessing `chrome://net-internals/#dns`

[2]: https://en.wikipedia.org/wiki/DNS_over_HTTPS
[3]: https://datatracker.ietf.org/doc/html/rfc1918
[4]: https://raw.githubusercontent.com/mageddo/dns-proxy-server/607af35d2fc985a8ad9b6cb4b7953f6e87335d97/doh/ca.crt
