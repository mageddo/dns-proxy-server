package testing.templates.docker;

import com.mageddo.dnsproxyserver.solver.AddressResolution;
import com.mageddo.net.IP;

import testing.templates.IpTemplates;

public class EntryTemplates {
  public static AddressResolution zeroIp() {
    return AddressResolution
        .builder()
        .ips(IP.of(IpTemplates.ZERO))
        .hostnameMatched(true)
        .build();
  }

  public static AddressResolution localIpv6() {
    return AddressResolution
        .builder()
        .hostnameMatched(true)
        .ips(IP.of(IpTemplates.LOCAL_EXTENDED_IPV6))
        .build();
  }

  public static AddressResolution hostnameMatchedButNoAddress() {
    return AddressResolution
        .builder()
        .hostnameMatched(true)
        .build()
        ;
  }

  public static AddressResolution hostnameNotMatched() {
    return AddressResolution
        .builder()
        .build()
        ;
  }
}
