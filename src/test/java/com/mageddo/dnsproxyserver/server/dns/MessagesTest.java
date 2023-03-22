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
    assertTrue(res.getHeader().getFlag(Flags.RA));
    assertEquals(Rcode.NXDOMAIN, res.getHeader().getRcode());

  }

  @Test
  void mustRespondRaFlagEvenWhenRdIsNotSetForARecord(){

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.aAnswer(query, "0.0.0.0");

    // assert
    assertFalse(query.getHeader().getFlag(Flags.QR));
    assertTrue(query.getHeader().getFlag(Flags.RD));

    final var resHeader = res.getHeader();
    assertTrue(resHeader.getFlag(Flags.QR));
    assertTrue(resHeader.getFlag(Flags.RA));
    assertEquals(Rcode.NOERROR, resHeader.getRcode());

  }

  @Test
  void mustRespondRaFlagEvenWhenRdIsNotSetForAAAARecord(){

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.quadAnswer(query, "0.0.0.0");

    // assert
    assertFalse(query.getHeader().getFlag(Flags.QR));
    assertTrue(query.getHeader().getFlag(Flags.RD));

    final var resHeader = res.getHeader();
    assertTrue(resHeader.getFlag(Flags.QR));
    assertTrue(resHeader.getFlag(Flags.RA));
    assertEquals(Rcode.NOERROR, resHeader.getRcode());

  }

}
