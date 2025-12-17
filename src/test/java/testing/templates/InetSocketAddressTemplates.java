package testing.templates;

import java.net.InetSocketAddress;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.IpAddr;
import com.mageddo.net.IpAddrs;

public class InetSocketAddressTemplates {
  public static InetSocketAddress _8_8_8_8() {
    return new InetSocketAddress(Ips.toAddress("8.8.8.8"), 53);
  }

  public static IpAddr _8_8_8_8_addr() {
    return IpAddrs.from(_8_8_8_8());
  }

  public static InetSocketAddress _8_8_4_4() {
    return new InetSocketAddress(Ips.toAddress("8.8.4.4"), 53);
  }

  public static IpAddr _1_1_1_1_addr() {
    return IpAddrs.from(_1_1_1_1());
  }

  public static InetSocketAddress _1_1_1_1() {
    return new InetSocketAddress(Ips.toAddress("1.1.1.1"), 53);
  }
}
