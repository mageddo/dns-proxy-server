package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleServer {

  private final UDPServer udpServer;
  private final TCPServer tcpServer;
  private final RequestHandler requestHandler;

  public void start(
    int port, Protocol protocol, List<Solver> solvers, InetAddress bindAddress
  ) {

    solvers.forEach(this.requestHandler::bind);
    this.start0(port, protocol, bindAddress);

  }

  void start0(int port, Protocol protocol, InetAddress bindAddress) {
    final var tcpHandler = new TCPHandler(this.requestHandler);
    switch (protocol) {
      case UDP -> this.udpServer.start(port, bindAddress);
      case TCP -> {
        this.tcpServer.start(port, bindAddress, tcpHandler);
      }
      default -> {
        this.udpServer.start(port, bindAddress);
        this.tcpServer.start(port, bindAddress, tcpHandler);
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
    private final byte[] buff = new byte[1024];
    private int offset = 0;

    TCPHandler(RequestHandler handler) {
      this.handler = handler;
    }

    @Override
    public void handle(byte[] data, int length, SocketClient client) {
      try {

        System.arraycopy(data, 0, this.buff, this.offset, length);
        this.offset += length;
        log.debug("status=append, length={}, offset={}", length, offset);

        final var reqMsg = new Message(ByteBuffer.wrap(this.buff, 0, this.offset));
        final var res = this.handler.handle(reqMsg, "tcp").toWire();
        client.getOut().write(res);
        client.getOut().flush();
        log.debug("status=success, req={}", Messages.simplePrint(reqMsg));
      } catch (Exception e) {
        log.warn(
          "status=request-failed, length={}, req={}, array={}, msg={}",
          length, new String(data, 0, length), Arrays.toString(data), e.getMessage(), e
        );
      } finally {
//        client.forceClose();
      }

    }
  }

}
