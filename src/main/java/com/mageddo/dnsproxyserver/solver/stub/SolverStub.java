package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigEntryTypes;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.net.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.mageddo.dns.utils.Messages.findQuestionTypeCode;

/**
 * Extract the address from the hostname then answer.
 * Inspired at nip.io and sslip.io, see #545.
 */

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverStub implements Solver {

  public static final String DOMAIN_NAME = ".docker";

  @Override
  public Response handle(Message query) {

    final var questionType = Messages.findQuestionType(query);
    if (ConfigEntryTypes.isNot(questionType, Config.Entry.Type.A, Config.Entry.Type.AAAA)) {
      log.debug("status=unsupportedType, type={}, query={}", findQuestionTypeCode(query), Messages.simplePrint(query));
      return null;
    }
    final var hostname = Messages.findQuestionHostname(query);
    if (!hostname.endsWith(DOMAIN_NAME)) {
      log.debug("status=hostnameDoesntMatch, type={}", hostname);
    }
    return null;
//      final var ip = this.machineService.findHostMachineIP(questionType.toVersion());
//      log.debug("status=solvingHostMachineName, host={}, ip={}", hostname, ip);
//      return Response.of(
//        Messages.answer(query, Objects.mapOrNull(ip, IP::toText), questionType.toVersion()),
//        Messages.DEFAULT_TTL_DURATION
//      );
//    }
  }
}
