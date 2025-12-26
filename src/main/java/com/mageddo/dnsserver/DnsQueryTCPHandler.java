package com.mageddo.dnsserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import com.mageddo.dns.utils.Messages;
import com.mageddo.utils.Shorts;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

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
        final var query = Messages.of(buff);
        final var res = this.handler.handle(query, "tcp")
            .toWire();

        final var out = client.getOut();
        out.write(Shorts.toBytes((short) res.length));
        out.flush();
        out.write(res);
        out.flush();

        if(log.isTraceEnabled()){
          log.trace(
              "status=success, queryMsgSize={}, resMsgSize={}, req={}",
              msgSize, res.length, Messages.simplePrint(query)
          );
        }

      }
    } catch (UncheckedIOException | IOException e) {
      log.debug(
          "status=socketClosed, runningTime={}, msg={}, class={}",
          client.getRunningTime(), e.getMessage(), ClassUtils.getSimpleName(e)
      );
    } catch (Exception e) {
      log.warn("status=request-failed, msg={}", e.getMessage(), e);
    } finally {
      MDC.clear();
    }
  }

  static byte[] readBodyAndValidate(InputStream in, short msgSize) {
    try {
      final var buff = new byte[msgSize];
      int offset = 0, read = 0;
      while (read != -1 && offset != msgSize) {
        final int left = msgSize - offset;
        read = in.read(buff, offset, left);
        offset += Math.max(read, 0);
      }

      Validate.isTrue(
          msgSize == offset,
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
          if (i >= 1) {
            log.info("status=incompleteHeader, bytes={}", i + 1);
          }
          return -1;
        }
        msgSizeBuf.put(i, read);
      }
      return msgSizeBuf.getShort();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
