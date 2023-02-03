package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.lang.regex.Regexes;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Value
public class IpAddr {

  public static final Pattern IP_ADDR_REGEX =
    Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})((?::(\\d+)|))$");

  IP ip;
  Integer port;

  /***
   *
   * @param addr something like 192.168.0.1 or 192.168.0.1:4411
   * @return parsed object.
   */
  public static IpAddr of(String addr) {
    final var groups = Regexes.groups(addr, IP_ADDR_REGEX);
    return new IpAddr(
      IP.of(groups.get(1)),
      groups.get(3, s -> StringUtils.isBlank(s) ? null : Integer.parseInt(s))
    );
  }
}
