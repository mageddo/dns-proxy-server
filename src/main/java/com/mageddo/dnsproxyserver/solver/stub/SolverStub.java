package com.mageddo.dnsproxyserver.solver.stub;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
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

  @Override
  public Response handle(Message query) {
    final var questionType = Messages.findQuestionType(query);
    if (ConfigEntryTypes.isNot(questionType, Type.A, Type.AAAA, Type.HTTPS)) {
      log.debug("status=unsupportedType, type={}, query={}", findQuestionTypeCode(query),
          Messages.simplePrint(query)
      );
      return null;
    }

    final var hostname = Messages.findQuestionHostname(query);
    final var domainName = this.findDomainName();
    if (!hostname.endsWith(domainName)) {
      log.debug("status=hostnameDoesntMatchRequiredDomain, hostname={}", hostname);
      return null;
    }

    if (questionType.isHttps()) {
      return Response.internalSuccess(Messages.notSupportedHttps(query));
    }

    final var foundIp = HostnameIpExtractor.safeExtract(hostname, domainName);
    if (foundIp == null) {
      log.debug("status=notSolved, hostname={}", hostname);
      return null;
    }

    final var qTypeVersion = questionType.toVersion();
    final var sameVersion = foundIp.versionIs(qTypeVersion);
    log.debug(
        "status=solved, host={}, ip={}, qTypeVersion={}",
        hostname, foundIp, qTypeVersion
    );
    return ResponseMapper.toDefaultSuccessAnswer(
        query, sameVersion ? foundIp : null, qTypeVersion
    );
  }

  String findDomainName() {
    return Configs.getInstance()
        .getSolverStub()
        .getDomainName();
  }
}
