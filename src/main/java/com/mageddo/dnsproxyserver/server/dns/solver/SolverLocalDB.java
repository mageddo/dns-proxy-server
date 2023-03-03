package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverLocalDB implements Solver {

  private final ConfigDAO configDAO;
  private final SolverDelegate solverDelegate;

  @Override
  public Message handle(Message query) {

    final var stopWatch = StopWatch.createStarted();

    final var type = Messages.findQuestionTypeCode(query);
    if (Type.isNot(type, Type.A, Type.CNAME, Type.AAAA)) {
      log.trace("status=typeNotSupported, action=continue, type={}, time={}", type, stopWatch.getTime());
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      stopWatch.split();
      final var entry = this.findEntryTo(host);
      if (entry == null) {
        log.trace(
            "status=partialNotFound, askedHost={}, time={}",
            askedHost, stopWatch.getTime() - stopWatch.getSplitTime()
        );
        continue;
      }
      log.trace(
          "status=found, type={}, askedHost={}, time={}, totalTime={}",
          entry.getType(), askedHost, stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime()
      );

      if (Type.is(entry.getType(), Type.A, Type.AAAA)) {
        return Messages.aAnswer(query, entry.getIp(), entry.getTtl());
      }
      return this.solverDelegate.solve(query, entry);
    }

    log.trace("status=notFound, askedHost={}, totalTime={}", askedHost, stopWatch.getTime());
    return null;
  }

  Config.Entry findEntryTo(Hostname host) {
    return this.configDAO.findEntryForActiveEnv(host);
  }

}
