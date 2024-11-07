package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;
import org.apache.commons.lang3.StringUtils;

public class Ipv6Parser implements Parser {
  @Override
  public IP parse(String addressExpression) {
    if (isIpv6(addressExpression)) {
      return IP.of(addressExpression.replaceAll("-", ":"));
    }
    throw new ParseException("Not ipv6 address: " + addressExpression);
  }

  private static boolean isIpv6(String addressExpression) {
    return addressExpression.contains("--") || StringUtils.countMatches(addressExpression, "-") >= IP.IPV4_BYTES;
  }
}
