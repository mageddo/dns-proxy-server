package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.docker.DockerRepository;
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

  private final DockerRepository dockerRepository;

  @Override
  public Message handle(Message reqMsg) {
    final var hostname = Messages.findQuestionHostname(reqMsg);
    final var config = Configs.getInstance();
    if (hostname.isEqualTo(config.getHostMachineHostname())) {
      final var ip = this.dockerRepository.findHostMachineIp();
      if (ip == null) {
        log.debug("status=hostMachineIpNotFound, host={}", hostname);
        return null;
      }
      log.debug("status=solvingHostMachineName, host={}, ip={}", hostname, ip);
      return Messages.aAnswer(reqMsg, ip);
    }
    return null;
  }

  @Override
  public byte priority() {
    return Priority.ZERO;
  }
}
