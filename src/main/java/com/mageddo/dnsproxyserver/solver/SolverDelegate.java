package com.mageddo.dnsproxyserver.solver;

import java.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Hostnames;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config;

import org.xbill.DNS.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Query all configured solvers to solve a cname address.
 *
 * @see SolverLocalDB
 */
@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverDelegate {

  private final SolverProvider solverProvider;

  public Response solve(Message query, Config.Entry entry) {
    log.debug("status=solvingCnameIp, source={}, target={}", entry.getHostname(),
        entry.getTarget()
    );

    final var cnameAnswer = cnameAnswer(query, entry);
    final var question = Messages.copyQuestionForNowHostname(query,
        Hostnames.toAbsoluteName(entry.getTarget())
    );

    final var solvers = this.solverProvider.getSolversExcluding(SolverLocalDB.class);
    for (final var solver : solvers) {
      final var res = solver.handle(question);
      if (res != null) {
        log.debug("status=cnameARecordSolved, host={}, r={}", entry.getHostname(),
            Messages.simplePrint(res)
        );
        return res.withMessage(Messages.combine(res.getMessage(), cnameAnswer));
      }
    }
    // answer only the cname, without the matching IP when no IP is found
    return Response.of(cnameAnswer, Duration.ofSeconds(entry.getTtl()));
  }

  static Message cnameAnswer(Message query, Config.Entry entry) {
    return Messages.cnameResponse(query, entry.getTtl(), entry.getTarget());
  }
}
