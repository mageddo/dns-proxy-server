package com.mageddo.dnsserver;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.utils.Shorts;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import testing.templates.MessageTemplates;
import testing.templates.SocketClientTemplates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DnsQueryTCPHandlerTest {

  RequestHandlerMock handler = new RequestHandlerMock();

  DnsQueryTCPHandler queryHandler = new DnsQueryTCPHandler(this.handler);

  @Test
  void mustReadEntireMessageBeforeHandleIt() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var querySize = query.toWire().length;
    final var out = new ByteArrayOutputStream();

    final var in = new PipedInputStream();
    final var queryOut = new PipedOutputStream(in);

    ThreadPool
      .scheduled()
      .schedule(
        () -> {

          writeMsgHeaderSlowly(queryOut, (short) querySize);

          final var data = query.toWire();
          writeQueryMsgSlowly(queryOut, data);

          // wait some time before "timeout"
          Threads.sleep(50);
          IOUtils.closeQuietly(queryOut);

        },
        50,
        TimeUnit.MILLISECONDS
      );

    final var client = SocketClientTemplates.buildWith(in, out);

    // act
    this.queryHandler.handle(client);

    // assert
    final var actualSize = out.size() - Short.BYTES;
    assertEquals(querySize, actualSize);
    assertEquals(querySize, Shorts.fromBytes(out.toByteArray(), 0));
    assertArrayEquals(
      query.toWire(),
      Arrays.copyOfRange(out.toByteArray(), 2, out.size()),
      String.format("%s <> %s", query, out)
    );

  }

  static void writeQueryMsgSlowly(OutputStream out, byte[] data) {
    try {
      final var middleIndex = data.length / 2;
      out.write(data, 0, middleIndex);
      Threads.sleep(100);
      out.write(data, middleIndex, data.length - middleIndex);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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
