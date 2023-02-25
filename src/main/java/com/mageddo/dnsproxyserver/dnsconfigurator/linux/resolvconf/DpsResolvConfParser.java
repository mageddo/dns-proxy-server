package com.mageddo.dnsproxyserver.dnsconfigurator.linux.resolvconf;

import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;
import com.mageddo.dnsproxyserver.server.dns.IP;

import java.nio.file.Path;

public class DpsResolvConfParser {

  public static void process(Path confFile, IP ip) {
    ResolvConfParser.process(confFile, new SetMachineDNSServerHandler(ip.raw()));
  }

  public static void restore(Path confFile) {
    ResolvConfParser.process(confFile, new DnsServerCleanerHandler());
  }
}
