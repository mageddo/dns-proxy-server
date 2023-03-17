package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
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
    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      final var ip = this.containerSolvingService.findBestHostIP(host);
      if (ip != null) {
        final var ttl = 30;
        return Response.of(Messages.aAnswer(query, ip, ttl), Duration.ofSeconds(ttl));
      }
    }

    return null;
  }

}
