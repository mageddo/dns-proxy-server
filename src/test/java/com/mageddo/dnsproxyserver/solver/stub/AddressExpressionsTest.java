package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dnsproxyserver.solver.stub.addressexpression.AddressExpressions;
import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressExpressionsTest {

  @Test
  void mustConvertIpv4ExpressionSplitByDots() {
    final var exp = "10.0.0.1";

    final var addr = AddressExpressions.toIp(exp);

    assertEquals(IP.of(exp), addr);
  }

  @Test
  void mustConvertIpv4ExpressionSplitByDash() {
    final var exp = "10-0-0-1";

    final var addr = AddressExpressions.toIp(exp);

    assertEquals(IP.of("10.0.0.1"), addr);
  }

  @Test
  void mustConvertIpv6ExpressionSplitByDash() {
    final var exp = "a--1";

    final var addr = AddressExpressions.toIp(exp);

    assertEquals(IP.of("a::1"), addr);
  }

  @Test
  void mustConvertExpandedIpv6ExpressionSplitByDash() {
    final var exp = "000a-0-0-0-0-0-0-0001";

    final var addr = AddressExpressions.toIp(exp);

    assertEquals(IP.of("a::1"), addr);
  }

  @Test
  void mustConvertHexadecimal() {
    final var exp = "0a000803";

    final var addr = AddressExpressions.toIp(exp);

    assertEquals(IP.of("10.0.8.3"), addr);
  }
}
