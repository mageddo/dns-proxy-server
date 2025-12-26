package testing.templates.docker;

import com.mageddo.dnsproxyserver.solver.docker.QueryResponse;
import com.mageddo.net.IP;

import testing.templates.IpTemplates;

public class EntryTemplates {
  public static QueryResponse zeroIp() {
    return QueryResponse
        .builder()
        .ip(IP.of(IpTemplates.ZERO))
        .hostnameMatched(true)
        .build();
  }

  public static QueryResponse localIpv6() {
    return QueryResponse
        .builder()
        .hostnameMatched(true)
        .ip(IP.of(IpTemplates.LOCAL_EXTENDED_IPV6))
        .build();
  }

  public static QueryResponse hostnameMatchedButNoAddress() {
    return QueryResponse
        .builder()
        .hostnameMatched(true)
        .build()
        ;
  }

  public static QueryResponse hostnameNotMatched() {
    return QueryResponse
        .builder()
        .build()
        ;
  }
}
