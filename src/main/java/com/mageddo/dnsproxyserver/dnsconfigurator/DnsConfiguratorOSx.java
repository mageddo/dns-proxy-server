package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfiguratorOSx implements DnsConfigurator {
  @Override
  public void configure(IpAddr addr) {

  }

  @Override
  public void restore() {

  }
}
