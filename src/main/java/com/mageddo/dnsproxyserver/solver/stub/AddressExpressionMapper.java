package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IpAddr;

public class AddressExpressionMapper {
  public static IpAddr toAddr(String addressExpression) {
    return IpAddr.of(addressExpression);
  }
}
