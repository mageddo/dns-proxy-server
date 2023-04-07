package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.config.Types;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import dagger.Lazy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

/**
 * When finds a wildcard hostname, delegate the found hostname to {@link SolverDelegate} class.
 */
@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverLocalDB implements Solver {

  public  static final String NAME = "SolverLocalDB";

  private final ConfigDAO configDAO;
  private final Lazy<SolverDelegate> solverDelegate;

  @Override
  public Response handle(Message query) {

    final var stopWatch = StopWatch.createStarted();

    final var type = Messages.findQuestionTypeCode(query);
    if (Types.isNot(type, Type.A, Type.CNAME, Type.AAAA)) {
      log.debug("status=typeNotSupported, action=continue, type={}, time={}", type, stopWatch.getTime());
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    final var questionType = Messages.findQuestionType(query);
    final var res = HostnameMatcher.match(askedHost, questionType.toVersion(), hostname -> {
      stopWatch.split();
      final var found = this.findEntryTo(hostname);
      if (found == null) {
        log.trace(
          "status=partialNotFound, askedHost={}, time={}",
          askedHost, stopWatch.getTime() - stopWatch.getSplitTime()
        );
        return null;
      }
      final var foundType = found.getType();
      log.trace(
        "status=found, type={}, askedHost={}, time={}, totalTime={}",
        foundType, askedHost, stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime()
      );

      if (foundType.isAddressSolving()) {
        final var ip = foundType.equals(questionType) ? found.requireTextIp(): null;
        return Response.of(
          Messages.answer(query, ip, questionType.toVersion(), found.getTtl()),
          Duration.ofSeconds(found.getTtl())
        );
      }
      return this.solverDelegate.get().solve(query, found);
    });
    if (res != null) {
      return res;
    }
    log.trace("status=notFound, askedHost={}, totalTime={}", askedHost, stopWatch.getTime());
    return null;
  }

  @Override
  public String name() {
    return NAME;
  }

  Config.Entry findEntryTo(HostnameQuery host) {
    return this.configDAO.findEntryForActiveEnv(host);
  }

}
