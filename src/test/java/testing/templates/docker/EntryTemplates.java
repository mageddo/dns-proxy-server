package testing.templates.docker;

import com.mageddo.dnsproxyserver.solver.docker.AddressRes;
import com.mageddo.net.IP;

import testing.templates.IpTemplates;

public class EntryTemplates {
  public static AddressRes zeroIp() {
    return AddressRes
        .builder()
        .ip(IP.of(IpTemplates.ZERO))
        .hostnameMatched(true)
        .build();
  }

  public static AddressRes localIpv6() {
    return AddressRes
        .builder()
        .hostnameMatched(true)
        .ip(IP.of(IpTemplates.LOCAL_EXTENDED_IPV6))
        .build();
  }

  public static AddressRes hostnameMatchedButNoAddress() {
    return AddressRes
        .builder()
        .hostnameMatched(true)
        .build()
        ;
  }

  public static AddressRes hostnameNotMatched() {
    return AddressRes
        .builder()
        .build()
        ;
  }
}
