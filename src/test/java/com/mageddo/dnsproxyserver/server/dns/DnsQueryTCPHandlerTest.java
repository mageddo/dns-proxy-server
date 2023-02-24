package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import com.mageddo.dnsproxyserver.templates.SocketClientTemplates;
import com.mageddo.utils.Shorts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;

class DnsQueryTCPHandlerTest {

  RequestHandlerMock handler = new RequestHandlerMock();

  DnsQueryTCPHandler queryHandler = new DnsQueryTCPHandler(this.handler);

  @Test
  void mustReadEntireMessageBeforeHandleIt() throws IOException {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var out = new ByteArrayOutputStream();

    final var in = new PipedInputStream();
    final var queryOut = new PipedOutputStream(in);

    ThreadPool
      .def()
      .schedule(
        () -> {
          writeMsgHeaderSlowly(queryOut, (short) query.numBytes());

          final var data = query.toWire();
          writeQueryMsgSlowly(out, data);

        },
        50,
        TimeUnit.MILLISECONDS
      );

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

  static void writeQueryMsgSlowly(ByteArrayOutputStream out, byte[] data) {
    final var middleIndex = data.length / 2;
    out.write(data, 0, middleIndex);
    Threads.sleep(30);
    out.write(data, middleIndex, data.length - middleIndex);
  }

  static void writeMsgHeaderSlowly(OutputStream out, short numBytes) {
    try {
      final var bytes = Shorts.toBytes(numBytes);
      out.write(bytes[0]);
      Threads.sleep(30);
      out.write(bytes[1]);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
