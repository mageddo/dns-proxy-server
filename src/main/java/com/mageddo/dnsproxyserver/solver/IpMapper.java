package com.mageddo.dnsproxyserver.solver;

import java.util.List;

import com.mageddo.commons.Collections;
import com.mageddo.commons.lang.Objects;
import com.mageddo.net.IP;

public class IpMapper {

  public static List<String> toText(List<IP> ips) {
    return Collections.mapNonNulls(ips, IpMapper::toText);
  }

  public static String toText(IP ip) {
    return ip != null ? ip.toText() : null;
  }

  public static List<String> toText(List<IP> ips, IP.Version version) {
    if (version == null) {
      return IpMapper.toText(ips);
    }
    return Objects.firstNonNull(ips, Collections.<IP>emptyList())
        .stream()
        .filter(ip -> ip.versionIs(version))
        .map(IpMapper::toText)
        .toList();
  }
}
