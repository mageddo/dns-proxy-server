package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

@Slf4j
@Singleton
public class UDPServer {

  public static final short BUFFER_SIZE = 512;

  private final ExecutorService pool;
  private final RequestHandler requestHandler;

  @Inject
  public UDPServer(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
    this.pool = ThreadPool.create();
  }

  public void start(int port, InetAddress bindAddress) {
    this.pool.submit(() -> this.start0(port, bindAddress));
    log.info("status=startingUdpServer, port={}, bindAddress={}", port, bindAddress);
  }

  private void start0(int port, InetAddress bindAddress) {
    try {
      final var server = new DatagramSocket(port, bindAddress);
      while (!server.isClosed()) {

        final var datagram = new DatagramPacket(new byte[BUFFER_SIZE], 0, BUFFER_SIZE);
        server.receive(datagram);

        this.pool.submit(() -> this.handle(server, datagram));

      }
    } catch (Exception e) {
      log.error("status=dnsServerStartFailed, port={}, msg={}", port, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  void handle(DatagramSocket server, DatagramPacket datagram) {
    try {
      final var reqMsg = new Message(datagram.getData());
      final var resData = this.requestHandler.handle(reqMsg, "udp").toWire();

      final var out = new DatagramPacket(resData, resData.length);
      out.setAddress(datagram.getAddress());
      out.setPort(datagram.getPort());
      server.send(out);
      log.debug(
        "status=success, dataLength={}, datagramLength={}, msg={}, data={}",
        datagram.getData().length, datagram.getLength(), new String(datagram.getData()), datagram.getData()
      );
    } catch (Exception e) {
      log.warn("status=messageHandleFailed, msg={}", e.getMessage(), e);
    }
  }

}
