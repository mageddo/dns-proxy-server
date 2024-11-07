package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;
import org.apache.commons.lang3.StringUtils;

public class Ipv4Parser implements Parser {
  @Override
  public IP parse(String addressExpression) {
    final var normalizedStr = addressExpression.replaceAll("-", ".");
    if (StringUtils.countMatches(normalizedStr, '.') == IP.IPV4_BYTES - 1) {
      return IP.of(normalizedStr);
    }
    throw new ParseException("invalid ipv4 address expression: " + addressExpression);
  }
}
