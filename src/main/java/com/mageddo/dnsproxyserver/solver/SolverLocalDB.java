package com.mageddo.dnsproxyserver.solver;

import java.util.EnumSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.dataprovider.MutableConfigDAO;
import com.mageddo.dnsproxyserver.solver.basic.QueryResponseHandler;
import com.mageddo.dnsproxyserver.solver.docker.AddressRes;

import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import dagger.Lazy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * When finds a wildcard hostname, delegate the found hostname to {@link SolverDelegate} class.
 */
@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverLocalDB implements Solver {

  public static final String NAME = "SolverLocalDB";

  private static final Set<Type> SUPPORTED_TYPES = EnumSet.of(
      Type.A, Type.CNAME, Type.AAAA, Type.HTTPS
  );

  private final QueryResponseHandler solver = QueryResponseHandler.builder()
      .solverName(NAME)
      .supportedTypes(SUPPORTED_TYPES)
      .build();

  private final MutableConfigDAO mutableConfigDAO;
  private final Lazy<SolverDelegate> cnameSolver;

  @Override
  public Response handle(Message query) {

    final var stopWatch = StopWatch.createStarted();

    return this.solver.ofResponse(query, hostname -> {

          stopWatch.split();
          final var askedHost = hostname.getHostname();
          final var found = this.findEntryTo(hostname);
          if (found == null) {
            log.trace(
                "status=partialNotFound, askedHost={}, time={}",
                askedHost, stopWatch.getTime() - stopWatch.getSplitTime()
            );
            return null;
          } else if (found.isCname()) {
            return this.cnameSolver.get()
                .solve(query, found);
          }

          return QueryResponseHandler.toResponse(
              query, AddressRes.matched(found.getIp(), found.getTtl())
          );

        }
    );

  }

  @Override
  public String name() {
    return NAME;
  }

  Config.Entry findEntryTo(HostnameQuery host) {
    return this.mutableConfigDAO.findEntryForActiveEnv(host);
  }

}
