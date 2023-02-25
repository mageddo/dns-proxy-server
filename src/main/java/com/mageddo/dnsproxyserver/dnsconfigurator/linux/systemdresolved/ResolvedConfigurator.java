package com.mageddo.dnsproxyserver.dnsconfigurator.linux.systemdresolved;

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
      new ConfigureDPSHandler(ip.raw())
    );
  }

  public static void restore(Path confFile) {
    ConfParser.process(
      confFile,
      createParser(),
      new CleanerHandler()
    );
  }

  static Function<String, EntryType> createParser() {
    throw new UnsupportedOperationException();
  }
}
