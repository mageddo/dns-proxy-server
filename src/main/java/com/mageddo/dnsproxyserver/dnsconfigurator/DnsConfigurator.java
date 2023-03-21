package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpSockAddr;

public interface DnsConfigurator {

  void configure(IpSockAddr addr);

  void restore();

}
