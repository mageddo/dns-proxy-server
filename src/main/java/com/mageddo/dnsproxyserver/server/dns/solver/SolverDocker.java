package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.Types;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.ContainerSolvingService;
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
  private final DockerFacade dockerFacade;

  @Override
  public Response handle(Message query) {

    if (!this.dockerFacade.isConnected()) {
      log.trace("status=dockerDisconnected");
      return null;
    }

    final var type = Messages.findQuestionType(query);
    if (Types.isNot(type, Type.AAAA, Type.A)) {
      log.trace("status=unsupportedType, type={}", type);
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    final var version = type.toVersion();
    return HostnameMatcher.match(askedHost, version, hostname -> {
      final var entry = this.containerSolvingService.findBestMatch(hostname);
      if (!entry.isHostnameMatched()) {
        return null;
      }
      return Response.of(Messages.answer(
        query,
        entry.getIpText(),
        hostname.getVersion()
      ));
    });

  }

}
