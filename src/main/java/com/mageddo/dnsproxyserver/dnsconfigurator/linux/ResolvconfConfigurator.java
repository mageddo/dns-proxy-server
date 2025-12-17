package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.nio.file.Path;
import java.util.function.Function;

import com.mageddo.conf.parser.ConfParser;
import com.mageddo.conf.parser.EntryType;
import com.mageddo.dnsproxyserver.utils.Dns;
import com.mageddo.net.IpAddr;

public class ResolvconfConfigurator {

  public static void process(Path confFile, IpAddr addr) {
    process(confFile, addr, true);
  }

  public static void process(Path confFile, IpAddr addr, boolean overrideNameServers) {

    Dns.validateIsDefaultPort(addr);

    ConfParser.process(
        confFile,
        createParser(),
        new ResolvconfConfigureDPSHandler(
            () -> String.format("nameserver %s # dps-entry", addr.getIp()
                .toText()
            ),
            overrideNameServers
        )
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
