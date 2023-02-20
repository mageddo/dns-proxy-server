package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.nio.ByteBuffer;
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

    TCPHandler(RequestHandler handler) {
      this.handler = handler;
    }

    @Override
    public void handle(byte[] data, int length, SocketClient client) {
      try {
        final var reqMsg = new Message(ByteBuffer.wrap(data, 0, length));
        final var res = this.handler.handle(reqMsg).toWire();
        client.getOut().write(res);
      } catch (Exception e) {
        log.warn("status=request-failed, msg={}", e.getMessage(), e);
      }

    }
  }

}
