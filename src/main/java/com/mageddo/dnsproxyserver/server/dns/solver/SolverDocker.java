package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SolverDocker implements Solver {

  private final ContainerSolvingService containerSolvingService;
  private final DockerDAO dockerDAO;

  @Override
  public Response handle(Message query) {

    if (!this.dockerDAO.isConnected()) {
      log.trace("status=dockerDisconnected");
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    return HostnameMatcher.match(askedHost, hostname -> {
      final var ip = this.containerSolvingService.findBestHostIP(hostname);
      return Objects.mapOrNull(ip, (it) -> Response.of(Messages.aAnswer(query, ip)));
    });

  }

}
