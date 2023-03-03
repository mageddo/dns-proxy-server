package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.ConfParser;
import com.mageddo.conf.parser.EntryType;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.DNS;
import org.apache.commons.lang3.Validate;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public class ResolvconfConfigurator {

  public static void process(Path confFile, IpAddr addr) {

    Validate.isTrue(
      Objects.equals(addr.getPort(), DNS.DEFAULT_PORT),
      "Resolvconf requires dns server port to be=%s, passedPort=%d",
      DNS.DEFAULT_PORT, addr.getPort()
    );

    ConfParser.process(
      confFile,
      createParser(),
      new ConfigureDPSHandler(() -> String.format("nameserver %s # dps-entry", addr.getIp().raw()))
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
