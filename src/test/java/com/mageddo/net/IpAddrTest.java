package com.mageddo.net;

import com.mageddo.dnsproxyserver.templates.IpAddrTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpAddrTest {

  @Test
  void mustParseIpv4AddrWithoutPort(){

    // arrange
    final var str = IpAddrTemplates.LOCAL;

    // act
    final var addr = IpAddr.of(str);

    // assert
    assertNotNull(addr);
    assertEquals(str, addr.getIp().toText());
    assertNull(addr.getPort());

  }

  @Test
  void mustParseIpv4AddrWithPort(){

    // arrange
    final var str = IpAddrTemplates.LOCAL_54;

    // act
    final var addr = IpAddr.of(str);

    // assert
    assertNotNull(addr);
    assertEquals(IpAddrTemplates.LOCAL, addr.getIp().toText());
    assertEquals(IpAddrTemplates.PORT_54, addr.getPort());

  }

  @Test
  void mustParseIpv6AddrWithPort(){

    // arrange
    final var str = IpAddrTemplates.LOCAL_IPV6_54;

    // act
    final var addr = IpAddr.of(str);

    // assert
    assertNotNull(addr);
    assertEquals(IpAddrTemplates.LOCAL_IPV6, addr.getIp().toText());
    assertEquals(IpAddrTemplates.PORT_54, addr.getPort());

  }

  @Test
  void mustParseIpv6AddrWithoutPort(){

    // arrange
    final var str = IpAddrTemplates.LOCAL_IPV6;

    // act
    final var addr = IpAddr.of(str);

    // assert
    assertNotNull(addr);
    assertEquals(str, addr.getIp().toText());
    assertNull(addr.getPort());

  }

  @Test
  void mustLeadWithNullIp(){

    // arrange
    final String str = null;

    // act
    final var addr = IpAddr.of(str);

    // assert
    assertNull(addr);

  }


  @Test
  void mustFailureWithInvalidValue(){

    // arrange
    final String str = "a";

    // act
    final var ex = assertThrows(RuntimeException.class, () -> {
      IpAddr.of(str);
    });

    // assert
    assertTrue(ex.getMessage().contains("valid IP"), ex.getMessage());

  }
}
