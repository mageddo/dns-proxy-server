package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.caching.LruTTLCache;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.function.Function;

@Slf4j
@Singleton
public class SolversCache {

  private final LruTTLCache cache = new LruTTLCache(2048, Duration.ofSeconds(5));

  public Message handle(Message reqMsg, Function<Message, Message> delegate) {
    final var key = String.valueOf(reqMsg.toString().hashCode());
    return this.cache.computeIfAbsent0(key, (k) -> {
      final var res = delegate.apply(reqMsg);
      final var ttl = Messages.findTTL(res);
      log.debug("status=hotLoad, k={}, ttl={}, simpleMsg={}", k, ttl, Messages.simplePrint(reqMsg));
      return Pair.of(res, ttl);
    });
  }

}
