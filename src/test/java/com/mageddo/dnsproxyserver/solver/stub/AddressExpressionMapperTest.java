package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IpAddr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressExpressionMapperTest {

  @Test
  void mustConvertIpv4ExpressionSplitByDots(){
    final var exp = "10.0.0.1";

    final var addr = AddressExpressionMapper.toAddr(exp);

    assertEquals(IpAddr.of(exp), addr);
  }
}
