package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

@Slf4j
public class UDPServer {

  public static final short BUFFER_SIZE = 512;

  private final ExecutorService pool;
  private final SocketAddress address;
  private final RequestHandler requestHandler;

  public UDPServer(SocketAddress address, RequestHandlerDefault requestHandler) {
    this.address = address;
    this.requestHandler = requestHandler;
    this.pool = ThreadPool.create();
  }

  public void start() {
    this.pool.submit(this::start0);
    log.trace("status=startingUdpServer, address={}", this.address);
  }

  private void start0() {
    try {
      final var server = new DatagramSocket(this.address);
      while (!server.isClosed()) {

        final var datagram = new DatagramPacket(new byte[BUFFER_SIZE], 0, BUFFER_SIZE);
        server.receive(datagram);

        this.pool.submit(() -> this.handle(server, datagram));

      }
    } catch (Exception e) {
      log.error("status=dnsServerStartFailed, address={}, msg={}", address, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  void handle(DatagramSocket server, DatagramPacket datagram) {
    try {
      final var reqMsg = new Message(datagram.getData());
      final var resData = this.requestHandler.handle(reqMsg, "udp").toWire();

      server.send(new DatagramPacket(resData, resData.length, datagram.getSocketAddress()));
      log.debug(
        "status=success, dataLength={}, datagramLength={}, serverAddr={}, clientAddr={}",
        datagram.getData().length, datagram.getLength(), server.getLocalAddress(), datagram.getSocketAddress()
      );
    } catch (Exception e) {
      log.warn("status=messageHandleFailed, msg={}", e.getMessage(), e);
    }
  }

  public SocketAddress getAddress() {
    return this.address;
  }
}
