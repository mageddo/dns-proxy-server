package com.mageddo.dnsproxyserver.dnsconfigurator.linux.resolvconf;

import com.mageddo.dnsproxyserver.resolvconf.DnsEntryType;
import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;

public class DnsServerCleanerHandler implements ResolvConfParser.Handler {

  @Override
  public String handle(String line, DnsEntryType entryType) {
    return switch (entryType) {
      case PROXY -> null;
      case COMMENTED_SERVER -> line.substring(2, line.indexOf(" # dps-comment"));
      default -> line;
    };
  }

  @Override
  public String after(boolean hasContent, boolean foundDps) {
    return null;
  }

}
