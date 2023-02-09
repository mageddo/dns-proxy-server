package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.resolvconf.DnsEntryType;
import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

import static com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser.buildDNSLine;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class LinuxDnsConfigurator implements DnsConfigurator {

  @Override
  public void configure(IP ip, Path conf) {
    ResolvConfParser.process(conf, new SetMachineDNSServerHandler(ip.raw()));
  }

  public static class SetMachineDNSServerHandler implements ResolvConfParser.Handler {

    private final String serverIP;

    public SetMachineDNSServerHandler(String serverIP) {
      this.serverIP = serverIP;
    }

    @Override
    public String handle(String line, DnsEntryType entryType) {
      return switch (entryType) {
        case PROXY -> buildDNSLine(this.serverIP);
        case SERVER -> String.format("# %s # dps-comment", line);
        default -> line;
      };
    }

    @Override
    public String after(boolean hasContent, boolean foundDps) {
      if (!hasContent || !foundDps) {
        return buildDNSLine(this.serverIP);
      }
      return null;
    }
  }

}
