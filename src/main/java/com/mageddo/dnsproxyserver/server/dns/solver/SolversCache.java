package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.caching.LruTTLCache;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionHostname;
import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionType;

@Slf4j
@Singleton
public class SolversCache {

  private final LruTTLCache cache = new LruTTLCache(2048, Duration.ofSeconds(5), false);

  public Message handle(Message query, Function<Message, Message> delegate) {
    final var key = buildKey(query);
    final var res = this.cache.computeIfAbsent0(key, (k) -> {
      log.trace("status=lookup, key={}, req={}", key, Messages.simplePrint(query));
      final var _res = delegate.apply(query);
      if (_res == null) {
        log.debug("status=noAnswer, k={}", k);
        return null;
      }
      final var ttl = Messages.findTTL(_res);
      log.debug("status=hotload, k={}, ttl={}, simpleMsg={}", k, ttl, Messages.simplePrint(query));
      return Pair.of(_res, ttl);
    });
    return Optional
      .ofNullable(res)
      .map(it -> Messages.matchId(query, it))
      .orElse(null);
  }

  static String buildKey(Message reqMsg) {
    final var type = findQuestionType(reqMsg);
    return String.format("%s-%s", type != null ? type : UUID.randomUUID(), findQuestionHostname(reqMsg));
  }

  public int getSize(){
    return this.cache.getSize();
  }
}
