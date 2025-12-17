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
}
