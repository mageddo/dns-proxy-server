package com.mageddo.dnsproxyserver.solver;

import java.util.EnumSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.solver.basic.QueryResponseHandler;
import com.mageddo.dnsproxyserver.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;

import org.xbill.DNS.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SolverDocker implements Solver {

  private static final Set<Type> SUPPORTED_TYPES = EnumSet.of(
      Type.AAAA, Type.A, Type.HTTPS
  );

  private final ContainerSolvingService containerSolvingService;

  private final DockerDAO dockerDAO;

  private final QueryResponseHandler solver = QueryResponseHandler.builder()
      .solverName(this.name())
      .supportedTypes(SUPPORTED_TYPES)
      .build();

  @Override
  public Response handle(Message query) {

    if (!this.dockerDAO.isConnected()) {
      log.trace("status=dockerDisconnected");
      return null;
    }

    return this.solver.ofQueryResponse(query, this.containerSolvingService::findBestMatch);
  }

}
