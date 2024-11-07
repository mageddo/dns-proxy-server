package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;
import org.apache.commons.lang3.StringUtils;

public class Ipv6Parser implements Parser {
  @Override
  public IP parse(String addressExpression) {
    if (isIpv6(addressExpression)) {
      try {
        return IP.of(addressExpression.replaceAll("-", ":"));
      } catch (RuntimeException e){
        throw throwError(addressExpression);
      }
    }
    throw throwError(addressExpression);
  }

  RuntimeException throwError(String addressExpression) {
    throw new ParseException("Not ipv6 address: " + addressExpression);
  }

  static boolean isIpv6(String addressExpression) {
    return (addressExpression.contains("--") || StringUtils.countMatches(addressExpression, "-") >= IP.IPV4_BYTES)
           && !addressExpression.contains(".");
  }
}
