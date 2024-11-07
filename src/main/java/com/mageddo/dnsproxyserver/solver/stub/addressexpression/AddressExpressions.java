package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;

import java.util.List;

public class AddressExpressions {

  public static IP toIp(String addressExpression) {
    RuntimeException lastException = null;
    for (final var parser : buildParsers()) {
      try {
        return parser.parse(addressExpression);
      } catch (ParseException e) {
        lastException = e;
      }
    }
    throw lastException;
  }

  static List<Parser> buildParsers() {
    return List.of(
      new Ipv6Parser(),
      new Ipv4Parser(),
      new HexadecimalParser()
    );
  }

}
