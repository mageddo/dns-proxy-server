package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.ConfParser;
import com.mageddo.conf.parser.EntryType;
import com.mageddo.dnsproxyserver.server.dns.IP;

import java.nio.file.Path;
import java.util.function.Function;

public class ResolvedConfigurator {

  public static void configure(Path confFile, IP ip) {
    ConfParser.process(
      confFile,
      createParser(),
      new ConfigureDPSHandler(() -> "DNS=" + ip.raw() + " # dps-entry")
    );
  }

  public static void restore(Path confFile) {
    ConfParser.process(
      confFile,
      createParser(),
      new CleanerHandler()
    );
  }

  private static Function<String, EntryType> createParser() {
    return line -> {
      if (line.endsWith(DpsTokens.DPS_ENTRY_COMMENT)) {
        return EntryTypes.DPS_SERVER_TYPE;
      } else if (line.startsWith("# DNS=") && line.endsWith(DpsTokens.COMMENT_END)) {
        return EntryTypes.COMMENTED_SERVER_TYPE;
      } else if (line.startsWith(DpsTokens.COMMENT)) {
        return EntryTypes.COMMENT_TYPE;
      } else if (line.startsWith("DNS=")) {
        return EntryTypes.SERVER_TYPE;
      } else {
        return EntryTypes.OTHER_TYPE;
      }
    };
  }
}
