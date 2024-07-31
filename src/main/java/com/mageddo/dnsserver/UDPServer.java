package com.mageddo.dnsserver;

import com.mageddo.commons.io.IoUtils;
import com.mageddo.dns.utils.Messages;
import com.mageddo.utils.Executors;
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
  private DatagramSocket server;

  public UDPServer(SocketAddress address, RequestHandler requestHandler) {
    this.address = address;
    this.requestHandler = requestHandler;
    this.pool = Executors.newThreadExecutor();
  }

  public void start() {
    this.pool.submit(this::start0);
    log.trace("status=startingUdpServer, address={}", this.address);
  }

  private void start0() {
    try {
      this.server = new DatagramSocket(this.address);
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
      final var query = new Message(datagram.getData());
      final var res = this.requestHandler.handle(query, "udp");
      final var resData = res.toWire();

      server.send(new DatagramPacket(resData, resData.length, datagram.getSocketAddress()));
      log.debug(
        "status=success, query={}, res={}, serverAddr={}, clientAddr={}, dataLength={}, datagramLength={}",
        Messages.simplePrint(query), Messages.simplePrint(res),
        server.getLocalAddress(), datagram.getSocketAddress(), datagram.getData().length, datagram.getLength()
      );
    } catch (Exception e) {
      log.warn("status=messageHandleFailed, msg={}", e.getMessage(), e);
    }
  }

  public SocketAddress getAddress() {
    return this.address;
  }

  public void stop() {
    IoUtils.silentClose(this.server);
    this.pool.shutdown();
  }
}
