package com.mageddo.dnsproxyserver.solver.basic;

import java.util.Set;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry;
import com.mageddo.dnsproxyserver.solver.HostnameMatcher;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.docker.QueryResponse;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
public class BasicSolver {

  @NonNull
  String solverName;

  @NonNull
  @Singular
  Set<Entry.Type> supportedTypes;

  public Response solve(Message query, AddressFinder finder) {

    final var type = Messages.findQuestionType(query);
    if (this.isNotSupported(type)) {
      log.trace("status=unsupportedType, solver={}, type={}", this.solverName, type);
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    final var version = type.toVersion();
    return HostnameMatcher.match(
        askedHost,
        version,
        hostnameQuery -> processMatch(query, finder, hostnameQuery, type)
    );
  }

  static Response processMatch(
      Message query, AddressFinder finder, HostnameQuery hostnameQuery, Entry.Type type
  ) {
    final var res = finder.find(hostnameQuery);
    if (res == null || res.isHostNameNotMatched()) {
      return null;
    } else if (type.isHttps()) {
      return Response.internalSuccess(Messages.notSupportedHttps(query));
    }
    return Response.internalSuccess(Messages.authoritativeAnswer(
        query,
        res.getIpText(),
        hostnameQuery.getVersion()
    ));
  }

  private boolean isNotSupported(Entry.Type type) {
    return !this.supportedTypes.contains(type);
  }

  @FunctionalInterface
  public static interface AddressFinder {
    QueryResponse find(HostnameQuery hostnameQuery);
  }
}
