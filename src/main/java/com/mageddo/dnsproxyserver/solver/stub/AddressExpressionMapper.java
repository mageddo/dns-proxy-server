package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IP;
import org.apache.commons.lang3.StringUtils;

public class AddressExpressionMapper {

  private static final int IPV4_COUNT = 4;

  public static IP toAddr(String addressExpression) {
    if(isIpv6(addressExpression)){
      return IP.of(addressExpression.replaceAll("-", ":"));
    }
    return IP.of(addressExpression.replaceAll("-", "."));
  }

  private static boolean isIpv6(String addressExpression) {
    return addressExpression.contains("--") || StringUtils.countMatches(addressExpression, "-") > IPV4_COUNT;
  }
}
