package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverDocker implements Solver {

  private final DockerDAO dockerDAO;

  @Override
  public Message handle(Message reqMsg) {

    if (!this.dockerDAO.isConnected()) {
      log.debug("status=dockerDisconnected");
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(reqMsg);
    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      final var ip = this.dockerDAO.findBestHostIP(host);
      if (ip == null) {
        return null;
      }
      return Messages.aAnswer(reqMsg, ip);
    }

    return null;
  }

  @Override
  public byte priority() {
    return Priority.ONE;
  }
}
