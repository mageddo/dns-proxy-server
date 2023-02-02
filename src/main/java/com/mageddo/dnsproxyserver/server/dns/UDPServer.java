package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.threads.ThreadPool;
import com.mageddo.dnsproxyserver.utils.Classes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
public class UDPServer {
  public static final short BUFFER_SIZE = 512;
  private final List<Solver> solvers;
  private final ExecutorService pool;

  public UDPServer() {
    this.solvers = new ArrayList<>();
    this.pool = ThreadPool.create(5);
  }

  public UDPServer bind(Solver solver) {
    this.solvers.add(solver);
    return this;
  }

  public void start(int port, InetAddress bindAddress) {
    Validate.isTrue(!this.solvers.isEmpty(), "At least one solver is required");
    this.pool.submit(() -> this.start0(port, bindAddress));
    log.info("status=starting.., port={}, bindAddress={}", port, bindAddress);
  }

  private void start0(int port, InetAddress bindAddress) {
    try {
      final var server = new DatagramSocket(port, bindAddress);
      final byte[] buff = new byte[BUFFER_SIZE];
      while (!server.isClosed()) {

        final var in = new DatagramPacket(buff, 0, buff.length);
        server.receive(in);
        final var reqMsg = new Message(in.getData());

        this.pool.submit(() -> this.res(server, this.solve(reqMsg), in.getAddress(), in.getPort()));

      }
    } catch (IOException e) {
      log.error("status=dnsServerStartFailed, port={}, msg={}", port, e.getMessage(), e);
      throw new UncheckedIOException(e);
    }
  }

  Message solve(Message reqMsg) {
    for (final var solver : this.solvers) {
      final var solverName = Classes.findSimpleName(solver);
      try {
        final var reqStr = simplePrint(reqMsg);
        log.debug("status=trySolve, solver={}, req={}", solverName, reqStr);
        final var res = solver.handle(reqMsg);
        if(res == null){
          log.debug("status=notSolved, solver={}, req={}", solverName, reqStr);
          continue;
        }
        log.debug("status=solved, solver={}, req={}, res={}", solverName, reqStr, simplePrint(res));
        return res;
      } catch (Exception e) {
        log.warn("status=solverFailed, solver={}, msg={}", solverName, e.getMessage(), e);
      }
    }
    return null;
  }

  void res(DatagramSocket server, Message handle, InetAddress address, int port) {
    try {
      final var response = handle.toWire();
      final var out = new DatagramPacket(response, response.length);
      out.setAddress(address);
      out.setPort(port);

      server.send(out);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
