package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IP;

import java.nio.file.Path;

public interface DnsConfigurator {
  void configure(IP ip, Path conf);
  void restore(Path conf);
}
