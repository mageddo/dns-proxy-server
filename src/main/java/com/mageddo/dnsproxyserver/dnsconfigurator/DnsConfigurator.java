package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;

public interface DnsConfigurator {

  void configure(IpAddr addr);

  void restore();

}
