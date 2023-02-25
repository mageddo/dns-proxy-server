package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.DpsTokens;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.EntryTypes;

public class CleanerHandler implements Transformer {

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType().name()) {
      case EntryTypes.DPS_SERVER -> null;
      case EntryTypes.COMMENTED_SERVER -> DpsTokens.uncomment(entry.getLine());
      default -> entry.getLine();
    };
  }

}
