---
title: Auto configure host default DNS
weight: 1
---

DPS will configure itself as the default machine DNS when running on standalone mode (not on docker).

### Activation

This feature is active by default and can be disabled by `defaultDns` json config or `-default-dns` commandline arg.

### Platform Support

Check the following table to understand the support at every platform:

✅: Fully supported, ❌: Not supported, ⚠️: Partially supported

| Platform | Support | Description                                                                                                                                                            |
|----------|:-------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Linux    |    ✅    | Will use system-resolved when avaible or `/etc/resolv.conf`, check `MG_RESOLVCONF` env check which file will be configured, see [running on Linux][2] for more details |
| MAC OSX  |    ✅    | DPS will use `networksetup` to configure the DNS, check [running on MAC][3] to see more details                                                                        |
| Windows  |    ✅    | Will configure available network interfaces to query DPS as the default DNS, you can see available networks by running `ncpa.cpl`                                      |


### Docker Limitations

When you run DPS on docker it won't be able to configure itself as the default host DNS depending on your environment,
so you will need to that manually, see [specific running it][5]
instructions for your platform for more details.

[1]: https://github.com/mageddo/dns-proxy-server/issues/326
[2]: en/1-getting-started/running-it/linux/
[3]: http://localhost:1313/en/1-getting-started/running-it/mac/#configuring-dps-as-default-dns-manually
[4]: https://github.com/mageddo/dns-proxy-server/issues/326
[5]: {{%relref "1-getting-started/running-it/_index.md#specific-instructions" %}}

