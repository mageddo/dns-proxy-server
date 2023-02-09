package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverSystem implements Solver {

  private final DockerDAO dockerDAO;

  @Override
  public Message handle(Message reqMsg) {
    final var hostname = Messages.findQuestionHostname(reqMsg);
    final var config = Configs.getInstance();
    if (hostname.isEqualTo(config.getHostMachineHostname())) { // fixme fazer case com hostname + search domain
      final var ip = this.dockerDAO.findHostMachineIp();
      if (ip == null) {
        log.debug("status=hostMachineIpNotFound, host={}", hostname);
        return null;
      }
      log.debug("status=solvingHostMachineName, host={}, ip={}", hostname, ip);
      return Messages.aAnswer(reqMsg, ip.raw());
    }
    return null;
  }

  @Override
  public byte priority() {
    return Priority.ZERO;
  }
}
