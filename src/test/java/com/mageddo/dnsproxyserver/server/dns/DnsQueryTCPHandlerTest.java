package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import com.mageddo.dnsproxyserver.templates.SocketClientTemplates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;

class DnsQueryTCPHandlerTest {

  RequestHandlerMock handler = new RequestHandlerMock();

  DnsQueryTCPHandler queryHandler = new DnsQueryTCPHandler(this.handler);

  @Test
  void mustReadEntireMessageBeforeHandleIt() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var in = new ByteArrayInputStream(query.toWire());
    final var out = new ByteArrayOutputStream();
    final var client = SocketClientTemplates.buildWith(in, out);

    // act
    this.queryHandler.handle(client);

    // assert
    assertArrayEquals(
      query.toWire(),
      out.toByteArray(),
      String.format("%s <> %s", query, out)
    );

  }
}
