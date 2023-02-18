package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.caching.LruTTLCache;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionHostname;
import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionType;

@Slf4j
@Singleton
public class SolversCache {

  private final LruTTLCache cache = new LruTTLCache(2048, Duration.ofSeconds(5));

  public Message handle(Message reqMsg, Function<Message, Message> delegate) {
    final var key = buildKey(reqMsg);
    final var res = this.cache.computeIfAbsent0(key, (k) -> {
      log.debug("status=lookup, key={}, req={}", key, Messages.simplePrint(reqMsg));
      final var _res = delegate.apply(reqMsg);
      final var ttl = Messages.findTTL(_res);
      log.debug("status=hotLoad, k={}, ttl={}, simpleMsg={}", k, ttl, Messages.simplePrint(reqMsg));
      return Pair.of(_res, ttl);
    });
    return Messages.copyAnswers(reqMsg, res);
  }

  static String buildKey(Message reqMsg) {
    final var type = findQuestionType(reqMsg);
    return String.format("%s-%s", type != null ? type : UUID.randomUUID(), findQuestionHostname(reqMsg));
  }

}
