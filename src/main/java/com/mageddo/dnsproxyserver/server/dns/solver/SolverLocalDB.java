package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
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

  @Override
  public Message handle(Message reqMsg) {

    final var stopWatch = StopWatch.createStarted();
    final var askedHost = Messages.findQuestionHostname(reqMsg);

    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      stopWatch.split();
      final var entry = this.configDAO.findEntryForActiveEnv(host.getName());
      if (entry == null) {
        log.trace(
          "status=partialNotFound, askedHost={}, time={}",
          askedHost, stopWatch.getTime() - stopWatch.getSplitTime()
        );
        return null;
      }
      log.trace(
        "status=found, askedHost={}, time={}, totalTime={}",
        askedHost, stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime()
      );
      return Messages.aAnswer(reqMsg, entry);
    }

    log.trace("status=notFound, askedHost={}, totalTime={}", askedHost, stopWatch.getTime());
    return null;
  }

  @Override
  public byte priority() {
    return Priority.TWO;
  }
}
