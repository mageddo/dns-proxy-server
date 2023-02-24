package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.utils.Shorts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

/**
 * Handles a TCP packet to a DNS query then sends the response back.
 */
@Slf4j
class DnsQueryTCPHandler implements SocketClientMessageHandler {

  private final RequestHandler handler;

  DnsQueryTCPHandler(RequestHandler handler) {
    this.handler = handler;
  }

  @Override
  public void handle(SocketClient client) {
    try {
      while (client.isOpen()) {

        final var in = client.getIn();
        final var msgSize = readHeaderAndValidate(in);
        if (msgSize == -1) {
          return;
        }
        final var buff = readBodyAndValidate(in, msgSize);

        final var query = new Message(buff);
        final var res = this.handler.handle(query, "tcp")
          .toWire();

        final var out = client.getOut();
        out.write(Shorts.toBytes((short) res.length));
        out.write(res);
        out.flush();

        log.debug(
          "status=success, queryMsgSize={}, resMsgSize={}, req={}",
          msgSize, res.length, Messages.simplePrint(query)
        );

      }
    } catch (Exception e) {
      log.warn("status=request-failed, msg={}", e.getMessage(), e);
    }
  }

  static byte[] readBodyAndValidate(InputStream in, short msgSize) {
    try {
      final var buff = new byte[msgSize];
      final var read = in.read(buff, 0, msgSize);
      Validate.isTrue(
        msgSize == read,
        "status=headerMsgSizeDifferentFromReadBytes!, haderMsgSize=%d, read=%d",
        msgSize, read
      );
      return buff;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static short readHeaderAndValidate(InputStream in) {
    try {
      final var msgSizeBuf = ByteBuffer.allocate(2);
      final int read = in.read(msgSizeBuf.array(), 0, msgSizeBuf.limit());
      if (read == -1) {
        return -1;
      }
      Validate.isTrue(
        read == msgSizeBuf.limit(),
        "Must read the exactly header size, read=%d, expected=%d", read, msgSizeBuf.limit()
      );
      return msgSizeBuf.getShort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
