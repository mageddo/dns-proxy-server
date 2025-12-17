package com.mageddo.net;

import java.util.regex.Pattern;

import com.mageddo.commons.regex.Regexes;

import org.apache.commons.lang3.StringUtils;

public class IpUtils {

  public static final Pattern IPV4_REGEX = Pattern.compile("^([\\.\\d]+)((?::(\\d+)|))$");
  public static final Pattern IPV6_REGEX = Pattern.compile("^\\[([:\\w]+)\\]((?::(\\d+)|))$");
  public static final int IP_ADDR_GROUP = 1;
  public static final int IP_PORT_GROUP = 3;

  public static boolean isIpv4(String addr) {
    return Regexes.matches(addr, IPV4_REGEX);
  }

  public static boolean isIpv6(String addr) {
    return Regexes.matches(addr, IPV6_REGEX);
  }

  public static String getIpv4AddressOnly(String addr) {
    final var groups = Regexes.groups(addr, IPV4_REGEX);
    return groups.get(IP_ADDR_GROUP);
  }

  public static String getIpv4Port(String addr) {
    final var groups = Regexes.groups(addr, IPV4_REGEX);
    return groups.get(IP_PORT_GROUP);
  }

  public static String getIpv6AddressOnly(String addr) {
    final var groups = Regexes.groups(addr, IPV6_REGEX);
    return groups.get(IP_ADDR_GROUP);
  }

  public static String getIpv6Port(String addr) {
    final var groups = Regexes.groups(addr, IPV6_REGEX);
    return groups.get(IP_PORT_GROUP);
  }

  public static IpAddr toIpAddr(String addr) {
    if (StringUtils.isBlank(addr)) {
      return null;
    }

    if (IpUtils.isIpv4(addr)) {
      final var groups = Regexes.groups(addr, IPV4_REGEX);
      return IpAddr.of(IP.of(groups.get(IP_ADDR_GROUP)), parsePort(groups.get(IP_PORT_GROUP)));
    } else if (Regexes.matches(addr, IPV6_REGEX)) {
      final var groups = Regexes.groups(addr, IPV6_REGEX);
      return IpAddr.of(IP.of(groups.get(IP_ADDR_GROUP)), parsePort(groups.get(IP_PORT_GROUP)));
    }
    return IpAddr.of(IP.of(addr));
  }

  private static Integer parsePort(final String s) {
    if (StringUtils.isBlank(s)) {
      return null;
    }
    return Integer.parseInt(s);
  }
}
