package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.SolversCache;
import com.mageddo.dnsproxyserver.threads.ThreadPool;
import com.mageddo.dnsproxyserver.utils.Classes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
public class UDPServer {

  public static final short BUFFER_SIZE = 512;

  private final List<Solver> solvers;
  private final ExecutorService pool;
  private final SolversCache cache;

  @Inject
  public UDPServer(SolversCache cache) {
    this.cache = cache;
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

      final var resData = this.solve(reqMsg).toWire();

      final var out = new DatagramPacket(resData, resData.length);
      out.setAddress(datagram.getAddress());
      out.setPort(datagram.getPort());
      server.send(out);

    } catch (Exception e) {
      log.warn("status=messageHandleFailed, msg={}", e.getMessage(), e);
    }
  }

  Message solve(Message reqMsg) {
    final var stopWatch = StopWatch.createStarted();
    try {
      final var r = Optional
        .ofNullable(this.cache.handle(reqMsg, this::solve0))
        .orElseGet(() -> buildDefaultRes(reqMsg));
      log.debug("status=solved, time={}, res={}", stopWatch.getTime(), simplePrint(r));
      return r;
    } catch (Exception e) {
      log.warn(
        "status=solverFailed, totalTime={}, eClass={}, msg={}",
        stopWatch.getTime(), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return buildDefaultRes(reqMsg);
    }
  }

  Message solve0(Message reqMsg) {
    final var stopWatch = StopWatch.createStarted();
    for (final var solver : this.solvers) {
      stopWatch.split();
      final var solverName = Classes.findSimpleName(solver);
      try {
        final var reqStr = simplePrint(reqMsg);
        log.debug("status=trySolve, solver={}, req={}", solverName, reqStr);
        final var res = solver.handle(reqMsg);
        if (res == null) {
          log.debug(
            "status=notSolved, currentSolverTime={}, totalTime={}, solver={}, req={}",
            stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName, reqStr
          );
          continue;
        }
        log.debug(
          "status=solved, currentSolverTime={}, totalTime={}, solver={}, req={}, res={}",
          stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName, reqStr, simplePrint(res)
        );
        return res;
      } catch (Exception e) {
        log.warn(
          "status=solverFailed, currentSolverTime={}, totalTime={}, solver={}, eClass={}, msg={}",
          stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName,
          ClassUtils.getSimpleName(e), e.getMessage(), e
        );
      }
    }
    return null;
  }

  public static Message buildDefaultRes(Message reqMsg) {
    return Messages.nxDomain(reqMsg); // if all failed and returned null, then return as can't find
  }

}
