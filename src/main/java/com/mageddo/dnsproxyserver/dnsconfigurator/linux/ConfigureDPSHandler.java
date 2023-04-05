package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;

import java.util.Set;
import java.util.function.Supplier;

public class ConfigureDPSHandler implements Transformer {

  private final Supplier<String> dpsDnsLineBuilder;
  private final boolean overrideNameServers;

  public ConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder) {
    this(dpsDnsLineBuilder, true);
  }

  public ConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder, boolean overrideNameServers) {
    this.dpsDnsLineBuilder = dpsDnsLineBuilder;
    this.overrideNameServers = overrideNameServers;
  }

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType().name()) {
      case EntryTypes.DPS_SERVER -> this.dpsDnsLineBuilder.get();
      case EntryTypes.SERVER -> {
        if (this.overrideNameServers) {
          yield DpsTokens.comment(entry.getLine());
        }
        yield entry.getLine();
      }
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
