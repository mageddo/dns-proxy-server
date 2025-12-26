package com.mageddo.dnsproxyserver.solver.docker;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.solver.QueryResponseHandler;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SupportedTypes;
import com.mageddo.dnsproxyserver.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;

import org.xbill.DNS.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SolverDocker implements Solver {

  public static final String NAME = "SolverDocker";

  private final ContainerSolvingService containerSolvingService;

  private final DockerDAO dockerDAO;

  private final QueryResponseHandler handler = QueryResponseHandler.builder()
      .solverName(this.name())
      .supportedTypes(SupportedTypes.ADDRESSES)
      .build();

  @Override
  public Response handle(Message query) {

    if (!this.dockerDAO.isConnected()) {
      log.trace("status=dockerDisconnected");
      return null;
    }

    return this.handler.mapDynamicFromResolution(query,
        this.containerSolvingService::findBestMatch
    );
  }

  @Override
  public String name() {
    return NAME;
  }
}
