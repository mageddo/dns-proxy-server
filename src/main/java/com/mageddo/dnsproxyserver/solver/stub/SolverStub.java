package com.mageddo.dnsproxyserver.solver.stub;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigEntryTypes;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.ResponseMapper;
import com.mageddo.dnsproxyserver.solver.Solver;

import org.xbill.DNS.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.dns.utils.Messages.findQuestionTypeCode;

/**
 * Extract the address from the hostname then answer.
 * Inspired at nip.io and sslip.io, see #545.
 */

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverStub implements Solver {

  public static final String DOMAIN_NAME = "stub";

  @Override
  public Response handle(Message query) {
    final var questionType = Messages.findQuestionType(query);
    if (ConfigEntryTypes.isNot(questionType, Config.Entry.Type.A, Config.Entry.Type.AAAA)) {
      log.debug("status=unsupportedType, type={}, query={}", findQuestionTypeCode(query),
          Messages.simplePrint(query)
      );
      return null;
    }

    final var hostname = Messages.findQuestionHostname(query);
    if (!hostname.endsWith(this.findDomainName())) {
      log.debug("status=hostnameDoesntMatchRequiredDomain, hostname={}", hostname);
      return null;
    }

    final var foundIp = HostnameIpExtractor.safeExtract(hostname, this.findDomainName());
    if (foundIp == null) {
      log.debug("status=notSolved, hostname={}", hostname);
      return null;
    }
    if (!foundIp.versionIs(questionType.toVersion())) {
      log.debug("status=incompatibleIpAndQueryType, hostname={}, questionType={}", hostname,
          questionType
      );
      return Response.nxDomain(query);
    }
    log.debug("status=solved, host={}, ip={}", hostname, foundIp);
    return ResponseMapper.toDefaultSuccessAnswer(query, foundIp, questionType.toVersion());
  }

  String findDomainName() {
    return Configs.getInstance()
        .getSolverStub()
        .getDomainName();
  }
}
