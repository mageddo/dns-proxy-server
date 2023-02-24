package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.utils.Shorts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.MDC;
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
      MDC.put("clientId", client.getId());
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

        try {
          final var out = client.getOut();
          out.write(Shorts.toBytes((short) res.length));
          out.flush();
          out.write(res);
          out.flush();
        } catch (IOException e) {
          log.info(
            "status=outIsClosed, query={}, msg={}, class={}",
            Messages.simplePrint(query), e.getMessage(), ClassUtils.getSimpleName(e)
          );
          break;
        }

        log.debug(
          "status=success, queryMsgSize={}, resMsgSize={}, req={}",
          msgSize, res.length, Messages.simplePrint(query)
        );

      }
    } catch (Exception e) {
      log.warn("status=request-failed, msg={}", e.getMessage(), e);
    } finally {
      MDC.clear();
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
      for (int i = 0; i < msgSizeBuf.limit(); i++) {
        final byte read = (byte) in.read();
        if (read == -1) {
          log.info("status=incompleteHeader, bytes={}", i + 1);
          return -1;
        }
        msgSizeBuf.put(i, read);
      }
      return msgSizeBuf.getShort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
