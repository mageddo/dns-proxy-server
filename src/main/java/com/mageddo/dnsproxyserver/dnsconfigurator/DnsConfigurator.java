package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.net.IpAddr;

public interface DnsConfigurator {

  void configure(IpAddr addr);

  void restore();

}
