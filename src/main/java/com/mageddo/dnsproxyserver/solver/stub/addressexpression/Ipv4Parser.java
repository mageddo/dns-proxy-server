package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.IP;

public class Ipv4Parser implements Parser {
  @Override
  public IP parse(String addressExpression) {
    final var normalizedStr = addressExpression.replaceAll("-", ".");
    if (Ips.isIpv4(normalizedStr)) {
      return IP.of(normalizedStr);
    }
    throw new ParseException("invalid ipv4 address expression: " + addressExpression);
  }

}
