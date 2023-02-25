package com.mageddo.dnsproxyserver.systemdresolved;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.dnsproxyserver.systemdresolved.DnsEntryType.COMMENT;
import static com.mageddo.dnsproxyserver.systemdresolved.DnsEntryType.COMMENTED_SERVER;
import static com.mageddo.dnsproxyserver.systemdresolved.DnsEntryType.DPS_SERVER;
import static com.mageddo.dnsproxyserver.systemdresolved.DnsEntryType.ELSE;
import static com.mageddo.dnsproxyserver.systemdresolved.DnsEntryType.SERVER;


public class ResolvedParser {

  public static void process(Path conf, Handler h) {
    process(conf, conf, h);
  }

  @SneakyThrows
  public static void process(Path source, Path target, Handler h) {
    String out;
    try (var r = Files.newBufferedReader(source)) {
      out = parse(r, h);
    }
    Files.writeString(target, out);
  }

  public static String parse(String in, Handler h) {
    return parse(new BufferedReader(new StringReader(in)), h);
  }

  @SneakyThrows
  public static String parse(BufferedReader r, Handler h) {

    final var sb = new StringBuilder();

    boolean hasContent = false, foundDnsProxyEntry = false;
    String line = null;
    while ((line = r.readLine()) != null) {
      hasContent = true;

      final var entryType = getDnsEntryType(line);
      if (entryType == DPS_SERVER) {
        foundDnsProxyEntry = true;
      }

      final var res = h.handle(line, entryType);
      if (StringUtils.isNotBlank(res)) {
        sb.append(res);
        sb.append('\n');
      }

    }

    final var res = h.after(hasContent, foundDnsProxyEntry);
    if (StringUtils.isNotBlank(res)) {
      sb.append(res);
      sb.append('\n');
    }
    return sb.toString();
  }

  static DnsEntryType getDnsEntryType(String line) {
    if (line.endsWith("# dps-entry")) {
      return DPS_SERVER;
    } else if (line.startsWith("# DNS=") && line.endsWith("# dps-comment")) {
      return COMMENTED_SERVER;
    } else if (line.startsWith("#")) {
      return COMMENT;
    } else if (line.startsWith("DNS=")) {
      return SERVER;
    } else {
      return ELSE;
    }
  }

  public static String buildDNSLine(String serverIP) {
    return "nameserver " + serverIP + " # dps-entry";
  }

  public interface Handler {

    String handle(String line, DnsEntryType entryType);

    String after(boolean hasContent, boolean foundDps);
  }

}
