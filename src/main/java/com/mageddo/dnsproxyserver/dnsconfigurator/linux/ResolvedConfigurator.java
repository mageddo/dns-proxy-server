package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.nio.file.Path;
import java.util.function.Function;

import com.mageddo.conf.parser.ConfParser;
import com.mageddo.conf.parser.EntryType;
import com.mageddo.dnsproxyserver.utils.Dns;
import com.mageddo.net.IpAddr;

public class ResolvedConfigurator {

  public static void configure(Path confFile, IpAddr addr) {
    ConfParser.process(
        confFile,
        createParser(),
        new ConfigureDPSHandler(() -> String.format("DNS=%s # dps-entry", formatAddr(addr)))
    );
  }

  private static String formatAddr(IpAddr addr) {
    if (Dns.isDefaultPortOrNull(addr)) {
      return addr.getRawIP();
    }
    return String.format("%s:%s", addr.getRawIP(), addr.getPort());
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
