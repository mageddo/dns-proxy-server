package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleServer {

  private final UDPServerPool udpServerPool;
  private final TCPServer tcpServer;
  private final RequestHandler requestHandler;

  public void start(int port, Protocol protocol, List<Solver> solvers) {

    solvers.forEach(this.requestHandler::bind);
    this.start0(port, protocol);

  }

  void start0(int port, Protocol protocol) {
    final var tcpHandler = new TCPHandler(this.requestHandler);
    switch (protocol) {
      case UDP -> this.udpServerPool.start(port);
      case TCP -> {
        this.tcpServer.start(port, null, tcpHandler);
      }
      default -> {
        this.udpServerPool.start(port);
        this.tcpServer.start(port, null, tcpHandler);
      }
    }
  }

  public enum Protocol {
    UDP,
    TCP,
    BOTH
  }

  static class TCPHandler implements SocketClientMessageHandler {

    private final RequestHandler handler;

    TCPHandler(RequestHandler handler) {
      this.handler = handler;
    }

    @Override
    public void handle(SocketClient client) {
      try {
        final var buff = new byte[512];
        while (client.isOpen()) {
          final var available = client.getIn().available();
          if (available == 0) {
//            Threads.sleep(SocketClient.FPS_60);
//            continue;
            break;
          }

          final var read = client.getIn().read(buff, 0, Math.min(available, buff.length));
          if (read == -1) {
            log.debug("status=streamEnded, time={}", client.getRunningTime());
            return;
          }

          final var msgSize = ByteBuffer
            .wrap(buff, 0, 2)
            .getShort();

          if (msgSize != read - 2) {
            log.warn("status=headerMsgSizeDifferentFromReadBytes!, haderMsgSize={}, read={}", msgSize, read - 2);
          }
          try {
            final var msgBuff = ByteBuffer.wrap(buff, 2, msgSize);
            final var reqMsg = new Message(msgBuff);
            final var res = this.handler.handle(reqMsg, "tcp").toWire();
            final var sizeArr = ByteBuffer
              .allocate(2)
              .putShort((short) res.length)
              .array();
            client.getOut().write(sizeArr);
            client.getOut().write(res);
            client.getOut().flush();

            log.debug(
              "status=success, reqMsgSize={}, resMsgSize={}, req={}",
              msgSize, res.length, Messages.simplePrint(reqMsg)
            );

          } catch (Exception e) {
            log.warn(
              "status=request-failed, length={}, msg={}",
              msgSize, e.getMessage(), e
            );
          }
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

  }

}
