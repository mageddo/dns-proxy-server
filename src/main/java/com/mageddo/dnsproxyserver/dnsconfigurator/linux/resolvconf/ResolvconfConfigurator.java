package com.mageddo.dnsproxyserver.dnsconfigurator.linux.resolvconf;

import com.mageddo.conf.parser.ConfParser;
import com.mageddo.conf.parser.EntryType;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.CleanerHandler;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ConfigureDPSHandler;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.DpsTokens;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.EntryTypes;
import com.mageddo.dnsproxyserver.server.dns.IP;

import java.nio.file.Path;
import java.util.function.Function;

public class ResolvconfConfigurator {

  public static void process(Path confFile, IP ip) {
    ConfParser.process(
      confFile,
      createParser(),
      new ConfigureDPSHandler(() -> "nameserver " + ip.raw() + " # dps-entry")
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
      } else if (line.startsWith("# nameserver ") && line.endsWith(DpsTokens.COMMENT_END)) {
        return EntryTypes.COMMENTED_SERVER_TYPE;
      } else if (line.startsWith(DpsTokens.COMMENT)) {
        return EntryTypes.COMMENT_TYPE;
      } else if (line.startsWith("nameserver")) {
        return EntryTypes.SERVER_TYPE;
      } else if (line.startsWith("search")) {
        return EntryTypes.SEARCH_TYPE;
      } else {
        return EntryTypes.OTHER_TYPE;
      }
    };
  }

}
