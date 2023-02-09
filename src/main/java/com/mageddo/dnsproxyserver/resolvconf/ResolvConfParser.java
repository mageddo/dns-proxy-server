package com.mageddo.dnsproxyserver.resolvconf;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.COMMENT;
import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.COMMENTED_SERVER;
import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.ELSE;
import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.PROXY;
import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.SEARCH;
import static com.mageddo.dnsproxyserver.resolvconf.DnsEntryType.SERVER;

public class ResolvConfParser {

  public static void process(Path resolvConfPath, Handler h) {
    process(resolvConfPath, resolvConfPath, h);
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
      if (entryType == PROXY) {
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
      return PROXY;
    } else if (line.startsWith("# nameserver ") && line.endsWith("# dps-comment")) {
      return COMMENTED_SERVER;
    } else if (line.startsWith("#")) {
      return COMMENT;
    } else if (line.startsWith("nameserver")) {
      return SERVER;
    } else if (line.startsWith("search")) {
      return SEARCH;
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
