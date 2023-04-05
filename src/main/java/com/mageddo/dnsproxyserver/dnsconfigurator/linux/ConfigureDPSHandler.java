package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;

import java.util.Set;
import java.util.function.Supplier;

public class ConfigureDPSHandler implements Transformer {

  private final Supplier<String> dpsDnsLineBuilder;

  public ConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder) {
    this.dpsDnsLineBuilder = dpsDnsLineBuilder;
  }

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType().name()) {
      case EntryTypes.DPS_SERVER -> this.dpsDnsLineBuilder.get();
      case EntryTypes.SERVER -> DpsTokens.comment(entry.getLine());
      default -> entry.getLine();
    };
  }

  @Override
  public String after(boolean fileHasContent, Set<String> foundEntryTypes) {
    if (!fileHasContent || !foundEntryTypes.contains(EntryTypes.DPS_SERVER)) {
      return this.dpsDnsLineBuilder.get();
    }
    return null;
  }

}
