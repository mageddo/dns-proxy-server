package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Rcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

  @Test
  void mustBuildCompliantNxResponse(){

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.nxDomain(query);

    // assert
    assertFalse(query.getHeader().getFlag(Flags.QR));
    assertTrue(res.getHeader().getFlag(Flags.QR));
    assertEquals(Rcode.NXDOMAIN, res.getHeader().getRcode());

  }

}
