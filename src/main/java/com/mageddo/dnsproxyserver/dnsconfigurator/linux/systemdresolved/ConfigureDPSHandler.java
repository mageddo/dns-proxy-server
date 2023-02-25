package com.mageddo.dnsproxyserver.dnsconfigurator.linux.systemdresolved;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.DpsTokens;

import java.util.Set;

public class ConfigureDPSHandler implements Transformer {

  private final String serverIP;

  public ConfigureDPSHandler(String serverIP) {
    this.serverIP = serverIP;
  }

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType().name()) {
      case EntryTypes.DPS_SERVER -> buildDNSLine(this.serverIP);
      case EntryTypes.SERVER -> DpsTokens.comment(entry.getLine());
      default -> entry.getLine();
    };
  }

  @Override
  public String after(boolean fileHasContent, Set<String> foundEntryTypes) {
    if (!fileHasContent || !foundEntryTypes.contains(EntryTypes.DPS_SERVER)) {
      return buildDNSLine(this.serverIP);
    }
    return null;
  }

  static String buildDNSLine(String serverIP) {
    return "DNS=" + serverIP + " # dps-entry";
  }
}
