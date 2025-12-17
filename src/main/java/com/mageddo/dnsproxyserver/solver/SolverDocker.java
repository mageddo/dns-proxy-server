package com.mageddo.dnsproxyserver.solver;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.ConfigEntryTypes;
import com.mageddo.dnsproxyserver.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;

import org.xbill.DNS.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SolverDocker implements Solver {

  private final ContainerSolvingService containerSolvingService;
  private final DockerDAO dockerDAO;

  @Override
  public Response handle(Message query) {

    if (!this.dockerDAO.isConnected()) {
      log.trace("status=dockerDisconnected");
      return null;
    }

    final var type = Messages.findQuestionType(query);
    if (ConfigEntryTypes.isNot(type, Type.AAAA, Type.A)) {
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
          return Response.internalSuccess(Messages.answer(
              query,
              entry.getIpText(),
              hostname.getVersion()
          ));
        }
    );

  }

}
