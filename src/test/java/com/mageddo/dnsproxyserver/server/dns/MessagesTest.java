package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dns.utils.Messages;

import org.junit.jupiter.api.Test;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Rcode;

import testing.templates.MessageTemplates;

import static com.mageddo.dns.utils.Messages.findFirstAnswerRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testing.templates.MessageTemplates.acmeAResponse;
import static testing.templates.MessageTemplates.acmeNxDomain;

class MessagesTest {

  @Test
  void mustBuildCompliantNxResponse() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.nxDomain(query);

    // assert
    assertFalse(query.getHeader()
        .getFlag(Flags.QR));
    assertTrue(res.getHeader()
        .getFlag(Flags.QR));
    assertTrue(res.getHeader()
        .getFlag(Flags.RA));
    assertEquals(Rcode.NXDOMAIN, res.getHeader()
        .getRcode()
    );

  }

  @Test
  void mustRespondRaFlagEvenWhenRdIsNotSetForARecord() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.aAnswer(query, "0.0.0.0");

    // assert
    assertFalse(query.getHeader()
        .getFlag(Flags.QR));
    assertTrue(query.getHeader()
        .getFlag(Flags.RD));

    final var resHeader = res.getHeader();
    assertTrue(resHeader.getFlag(Flags.QR));
    assertTrue(resHeader.getFlag(Flags.RA));
    assertEquals(Rcode.NOERROR, resHeader.getRcode());

  }

  @Test
  void mustRespondRaFlagEvenWhenRdIsNotSetForAAAARecord() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = Messages.quadAnswer(query, "0.0.0.0");

    // assert
    assertFalse(query.getHeader()
        .getFlag(Flags.QR));
    assertTrue(query.getHeader()
        .getFlag(Flags.RD));

    final var resHeader = res.getHeader();
    assertTrue(resHeader.getFlag(Flags.QR));
    assertTrue(resHeader.getFlag(Flags.RA));
    assertEquals(Rcode.NOERROR, resHeader.getRcode());

  }

  @Test
  void mustBuildSimplePrintReq() {
    // arrange
    final var msg = MessageTemplates.acmeAQuery();

    // act
    final var str = Messages.simplePrint(msg);

    // assert
    assertEquals("""
            query=A:acme.com""",
        str
    );
  }

  @Test
  void mustBuildSimpleAnswer() {
    // arrange
    final var answer = findFirstAnswerRecord(acmeAResponse());

    // act
    final var str = Messages.simplePrint(answer);

    // assert
    assertEquals("""
            acme.com.    30  IN  A  10.10.0.1""",
        str
    );
  }

  @Test
  void mustBuildSimplePrintResponse() {
    // arrange
    final var res = acmeAResponse();

    // act
    final var str = Messages.simplePrint(res);

    // assert
    assertEquals("""
            rc=0, res=acme.com.    30  IN  A  10.10.0.1""",
        str
    );
  }

  @Test
  void mustBuildSimplePrintNxDomainResponse() {
    // arrange
    final var res = acmeNxDomain();

    // act
    final var str = Messages.simplePrint(res);

    // assert
    assertEquals("""
            rc=3, query=A:acme.com""",
        str
    );
  }

  @Test
  void mustAddAnswerAndKeepId(){

    final var query = MessageTemplates.acmeAQuery();

    final var res = Messages.answer(query, "10.10.10.1");

    final var resId = Messages.getId(res);
    assertTrue(resId > 0);
    assertEquals(Messages.getId(query), resId);

  }
}
