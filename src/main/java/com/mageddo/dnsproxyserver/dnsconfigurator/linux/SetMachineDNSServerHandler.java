package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.resolvconf.DnsEntryType;
import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;

import static com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser.buildDNSLine;

public class SetMachineDNSServerHandler implements ResolvConfParser.Handler {

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
