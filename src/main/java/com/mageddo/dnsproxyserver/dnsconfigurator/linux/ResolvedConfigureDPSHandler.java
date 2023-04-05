package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;

import java.util.Set;
import java.util.function.Supplier;

public class ResolvedConfigureDPSHandler implements Transformer {

  private final Supplier<String> dpsDnsLineBuilder;
  private final boolean overrideNameServers;
  private boolean dpsSet = false;

  public ResolvedConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder) {
    this(dpsDnsLineBuilder, true);
  }

  public ResolvedConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder, boolean overrideNameServers) {
    this.dpsDnsLineBuilder = dpsDnsLineBuilder;
    this.overrideNameServers = overrideNameServers;
  }

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType().name()) {
      case EntryTypes.DPS_SERVER -> this.dpsDnsLineBuilder.get();
      case EntryTypes.SERVER -> {
        if (!this.overrideNameServers) {
          if (!this.dpsSet) {
            this.dpsSet = true;
            yield String.format("%s%n%s", this.dpsDnsLineBuilder.get(), entry.getLine());
          }
          yield entry.getLine();
        }
        yield entry.getLine();
      }
      default -> entry.getLine();
    };
  }

  @Override
  public String after(boolean fileHasContent, Set<String> foundEntryTypes) {
    if (
      !fileHasContent
        || (!foundEntryTypes.contains(EntryTypes.DPS_SERVER) && !foundEntryTypes.contains(EntryTypes.SERVER))
    ) {
      return this.dpsDnsLineBuilder.get();
    }
    return null;
  }

}
