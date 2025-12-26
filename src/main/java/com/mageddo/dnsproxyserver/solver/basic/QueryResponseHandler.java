package com.mageddo.dnsproxyserver.solver.basic;

import java.util.Set;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry;
import com.mageddo.dnsproxyserver.solver.HostnameMatcher;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.docker.AddressRes;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
public class QueryResponseHandler {

  @NonNull
  String solverName;

  @NonNull
  @Singular
  Set<Entry.Type> supportedTypes;

  public Response ofResponse(Message query, Function<Response> finder) {

    final var type = Messages.findQuestionType(query);
    if (this.isNotSupported(type)) {
      log.trace("status=unsupportedType, solver={}, type={}", this.solverName, type);
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    final var version = type.toVersion();
    return HostnameMatcher.match(askedHost, version, finder::apply);
  }

  public Response ofQueryResponse(Message query, Function<AddressRes> finder) {

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
        hostnameQuery -> toResponse(query, finder, hostnameQuery, type)
    );
  }

  static Response toResponse(
      Message query,
      Function<AddressRes> finder,
      HostnameQuery hostnameQuery,
      Entry.Type type
  ) {
    final var res = finder.apply(hostnameQuery);
    return toResponse(query, res, type);
  }

  public static Response toResponse(Message query, AddressRes res) {
    final var type = Messages.findQuestionType(query);
    return toResponse(query, res, type);
  }

  public static Response toResponse(Message query, AddressRes res, Entry.Type type) {
    if (res == null || res.isHostNameNotMatched()) {
      return null;
    } else if (type.isHttps()) {
      return Response.internalSuccess(Messages.notSupportedHttps(query));
    }
    final var ttl = res.getTTLDuration(Response.DEFAULT_NXDOMAIN_TTL);
    final var msg = Messages.authoritativeAnswer(
        query,
        res.getIp(type),
        type,
        ttl.toSeconds()
    );
    return Response.of(msg, ttl);
  }


  private boolean isNotSupported(Entry.Type type) {
    return !this.supportedTypes.contains(type);
  }

  @FunctionalInterface
  public static interface Function<T> {
    T apply(HostnameQuery hostnameQuery);
  }
}
