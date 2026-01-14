package testing.templates;

import com.mageddo.net.IP;

public class IpTemplates {

  public static final String LOCAL = "10.10.0.1";
  public static final String LOCAL_IPV6 = "2001:db8:1::2";
  public static final String LOCAL_EXTENDED_IPV6 = "2001:db8:1:0:0:0:0:2";
  public static final String LOCAL_192 = "192.168.0.10";
  public static final String ZERO = "0.0.0.0";

  public static IP local() {
    return IP.of(LOCAL);
  }

  public static IP loopback() {
    return IP.of("127.0.0.1");
  }

  public static IP localIpv6() {
    return IP.of(LOCAL_IPV6);
  }

  public static IP localIpv6_3() {
    return IP.of("2001:db8:1:0:0:0:0:3");
  }

  public static IP local_192() {
    return IP.of(LOCAL_192);
  }
}
