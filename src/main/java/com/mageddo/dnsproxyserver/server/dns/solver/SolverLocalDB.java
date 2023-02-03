package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    final var askedHost = Messages.findQuestionHostname(reqMsg);
    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      final var entry = this.configDAO.findEntryForActiveEnv(host.getName());
      if (entry == null) {
        log.trace("status=partialNotFound, askedHost={}", askedHost);
        return null;
      }
      log.trace("status=found, askedHost={}", askedHost);
      return Messages.aAnswer(reqMsg, entry);
    }
    log.trace("status=notFound, askedHost={}", askedHost);
    return null;
  }

  @Override
  public byte priority() {
    return Priority.TWO;
  }
}
