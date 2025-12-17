package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.util.Set;
import java.util.function.Supplier;

import com.mageddo.conf.parser.Entry;
import com.mageddo.conf.parser.Transformer;

public class ResolvconfConfigureDPSHandler implements Transformer {

  private final Supplier<String> dpsDnsLineBuilder;
  private final boolean overrideNameServers;
  private boolean dpsSet = false;

  public ResolvconfConfigureDPSHandler(Supplier<String> dpsDnsLineBuilder,
      boolean overrideNameServers) {
    this.dpsDnsLineBuilder = dpsDnsLineBuilder;
    this.overrideNameServers = overrideNameServers;
  }

  @Override
  public String handle(Entry entry) {
    return switch (entry.getType()
        .name()) {
      case EntryTypes.DPS_SERVER -> {
        this.dpsSet = true;
        yield this.dpsDnsLineBuilder.get();
      }
      case EntryTypes.SERVER -> {
        if (!this.overrideNameServers) {
          if (!this.dpsSet) {
            this.dpsSet = true;
            yield String.format("%s%n%s", this.dpsDnsLineBuilder.get(), entry.getLine());
          }
          yield entry.getLine();
        }
        yield DpsTokens.comment(entry.getLine());
      }
      default -> entry.getLine();
    };
  }

  @Override
  public String after(boolean fileHasContent, Set<String> foundEntryTypes) {
    if (
        !fileHasContent
            || (!has(foundEntryTypes, EntryTypes.DPS_SERVER) && this.overrideNameServers)
            || (!has(foundEntryTypes, EntryTypes.DPS_SERVER) && !has(foundEntryTypes,
            EntryTypes.SERVER
        ) && !this.overrideNameServers)
    ) {
      return this.dpsDnsLineBuilder.get();
    }
    return null;
  }

  private static boolean has(Set<String> foundEntryTypes, final String type) {
    return foundEntryTypes.contains(type);
  }

}
