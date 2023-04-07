package testing.templates;

import com.mageddo.net.IpAddr;

public class IpAddrTemplates {

  public static final int PORT_54 = 54;

  public static final String LOCAL_54 = "10.10.0.1:54";
  public static final String LOCAL = "10.10.0.1";

  public static final String LOCAL_IPV6 = IpTemplates.LOCAL_EXTENDED_IPV6;
  public static final String LOCAL_IPV6_54 = "[2001:db8:1:0:0:0:0:2]:54";

  public static IpAddr local() {
    return IpAddr.of(LOCAL);
  }

  public static IpAddr localPort54() {
    return IpAddr.of(LOCAL_54);
  }

  public static IpAddr loopback() {
    return IpAddr.of(IpTemplates.loopback());
  }
}
