package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.dnsconfigurator.linux.DnsConfiguratorLinux;
import com.mageddo.dnsproxyserver.server.dns.IpSockAddr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;


@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfiguratorOSX implements DnsConfigurator {

  private final DnsConfiguratorDefault configuratorDefault;
  private final DnsConfiguratorLinux configuratorLinux;

  @Override
  public void configure(IpSockAddr addr) {
    this.configuratorDefault.configure(addr);
    this.configuratorLinux.configure(addr);
  }

  @Override
  public void restore() {
    this.configuratorDefault.restore();
    this.configuratorLinux.restore();
  }
}
