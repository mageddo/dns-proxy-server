package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurator;
import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class LinuxDnsConfigurator implements DnsConfigurator {

  @Override
  public void configure(IP ip, Path conf) {
    ResolvConfParser.process(conf, new SetMachineDNSServerHandler(ip.raw()));
  }

  @Override
  public void restore(Path conf) {
    ResolvConfParser.process(conf, new DnsServerCleanerHandler());
    log.debug("status=restoredResolvConf, path={}", conf);
  }

}
