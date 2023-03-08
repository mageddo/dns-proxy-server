package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;


@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfiguratorOSX implements DnsConfigurator {

  private final DnsConfiguratorDefault configuratorDefault;
//  private final DnsConfiguratorLinux configuratorLinux; // todo also use linux configurator to configure resolvconf

  @Override
  public void configure(IpAddr addr) {
    this.configuratorDefault.configure(addr);
  }

  @Override
  public void restore() {
    this.configuratorDefault.restore();
  }
}
