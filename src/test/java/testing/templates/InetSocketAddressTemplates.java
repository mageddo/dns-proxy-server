package testing.templates;

import com.mageddo.dnsproxyserver.utils.Ips;

import java.net.InetSocketAddress;

public class InetSocketAddressTemplates {
  public static InetSocketAddress _8_8_8_8(){
    return new InetSocketAddress(Ips.toAddress("8.8.8.8"), 53);
  }

  public static InetSocketAddress _8_8_4_4() {
    return new InetSocketAddress(Ips.toAddress("8.8.4.4"), 53);
  }
}
