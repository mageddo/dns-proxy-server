package testing.templates.docker;

import com.mageddo.dnsproxyserver.solver.docker.Entry;
import com.mageddo.net.IP;

import testing.templates.IpTemplates;

public class EntryTemplates {
  public static Entry zeroIp() {
    return Entry
        .builder()
        .ip(IP.of(IpTemplates.ZERO))
        .hostnameMatched(true)
        .build();
  }

  public static Entry localIpv6() {
    return Entry
        .builder()
        .hostnameMatched(true)
        .ip(IP.of(IpTemplates.LOCAL_EXTENDED_IPV6))
        .build();
  }

  public static Entry hostnameMatchedButNoAddress() {
    return Entry
        .builder()
        .hostnameMatched(true)
        .build()
        ;
  }

  public static Entry hostnameNotMatched() {
    return Entry
        .builder()
        .build()
        ;
  }
}
