package com.mageddo.dnsproxyserver.solver;

import java.util.Set;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry;

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

  public Response mapDynamicFromResponse(Message query, Function<HostnameQuery, Response> finder) {

    final var type = Messages.findQuestionType(query);
    if (this.isNotSupported(type)) {
      log.trace("status=unsupportedType, solver={}, type={}", this.solverName, type);
      return null;
    }

    final var askedHost = Messages.findQuestionHostname(query);
    final var version = type.toVersion();
    return HostnameMatcher.match(askedHost, version, finder::apply);
  }

  public Response mapDynamicFromResolution(Message query, Function<HostnameQuery, AddressResolution> finder) {

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
        hostnameQuery -> map(query, finder, hostnameQuery, type)
    );
  }

  public Response mapExactFromResolution(Message query, Function<HostnameQuery, AddressResolution> fn) {

    final var type = Messages.findQuestionType(query);
    if (this.isNotSupported(type)) {
      log.trace("status=unsupportedType, solver={}, type={}", this.solverName, type);
      return null;
    }

    final var hostnameQuery = HostnameQuery.of(Messages.findQuestionHostname(query));
    return map(query, fn, hostnameQuery, type);
  }

  static Response map(
      Message query,
      Function<HostnameQuery, AddressResolution> finder,
      HostnameQuery hostnameQuery,
      Entry.Type type
  ) {
    final var res = finder.apply(hostnameQuery);
    return map(query, res, type);
  }

  public static Response map(Message query, AddressResolution res) {
    final var type = Messages.findQuestionType(query);
    return map(query, res, type);
  }

  public static Response map(Message query, AddressResolution res, Entry.Type type) {
    if (res == null || res.isHostNameNotMatched()) {
      return null;
    } else if (type.isHttps()) {
      return Response.internalSuccess(Messages.notSupportedHttps(query));
    }
    final var ttl = res.getTTL(Messages.DEFAULT_TTL_DURATION);
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
  public static interface Function<From, To> {
    To apply(From from);
  }
}
