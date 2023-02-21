package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.Hostnames;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * When {@link SolverLocalDB} finds a wildcard hostname, delegate the found hostname to this class.
 */
@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverDelegate {

  private final SolverProvider solverProvider;

  public Message solve(Message query, Config.Entry entry){
    log.debug("status=solvingCnameIp, source={}, target={}", entry.getHostname(), entry.getTarget());

    final var cnameAnswer = cnameAnswer(query, entry);
    final var question = Messages.copyQuestionForNowHostname(query, Hostnames.toAbsoluteName(entry.getTarget()));

    for (final var solver : this.solverProvider.getSolversExcludingLocalDB()) {
      final var res = solver.handle(question);
      if (res != null) {
        log.debug("status=cnameARecordSolved, host={}, r={}", entry.getHostname(), Messages.simplePrint(res));
        return Messages.combine(res, cnameAnswer);
      }
    }
    // answer only the cname, without the matching IP when a IP is not found
    return cnameAnswer;
  }

  static Message cnameAnswer(Message query, Config.Entry entry) {
    return Messages.cnameResponse(query, entry.getTtl(), entry.getTarget());
  }
}
