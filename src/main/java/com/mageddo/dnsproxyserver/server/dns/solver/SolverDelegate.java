package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverDelegate {

  private final SolverProvider solverProvider;

  public Message solve(Message reqMsg, Config.Entry entry){
    log.debug("status=solvingCnameIp, source={}, target={}", entry.getHostname(), entry.getTarget());
    final var cnameAnswer = Messages.cnameAnswer(reqMsg, entry);
    for (final var solver : this.solverProvider.getSolversExcludingLocalDB()) {
      final var aRes = solver.handle(reqMsg);
      if (aRes != null) {
        log.debug("status=cnameARecordSolved, host={}, r={}", entry.getHostname(), Messages.simplePrint(aRes));
        return Messages.combine(aRes, cnameAnswer);
      }
    }
    return cnameAnswer;
  }
}
