package com.mageddo.dnsproxyserver.solver.stub;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SupportedTypes;
import com.mageddo.dnsproxyserver.solver.basic.QueryResponseHandler;
import com.mageddo.dnsproxyserver.solver.docker.AddressResolution;

import org.xbill.DNS.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Extract the address from the hostname then answer.
 * Inspired at nip.io and sslip.io, see #545.
 */

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverStub implements Solver {

  private final QueryResponseHandler handler = QueryResponseHandler.builder()
      .solverName(this.name())
      .supportedTypes(SupportedTypes.ADDRESSES)
      .build();

  @Override
  public Response handle(Message query) {

    return this.handler.mapExactFromResolution(query, hostnameQuery -> {

      final var hostname = hostnameQuery.getHostname();
      final var domainName = this.findDomainName();
      if (!hostname.endsWith(domainName)) {
        log.debug("status=hostnameDoesntMatchRequiredDomain, hostname={}", hostname);
        return null;
      }

      final var foundIp = HostnameIpExtractor.safeExtract(hostname, domainName);
      if (foundIp == null) {
        log.debug("status=notSolved, hostname={}", hostname);
        return null;
      }
      return AddressResolution.matched(foundIp, Response.DEFAULT_SUCCESS_TTL);

    });

  }

  String findDomainName() {
    return Configs.getInstance()
        .getSolverStub()
        .getDomainName();
  }
}
