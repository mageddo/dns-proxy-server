package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.caching.LruTTLCache;
import com.mageddo.commons.lang.Objects;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionHostname;
import static com.mageddo.dnsproxyserver.server.dns.Messages.findQuestionType;

@Slf4j
@RequiredArgsConstructor
public class SolverCache {

  private final LruTTLCache cache = new LruTTLCache(2048, Duration.ofSeconds(5), false);

  @NonNull
  private final Name name;

  public Message handle(Message query, Function<Message, Response> delegate) {
    return Objects.mapOrNull(this.handleRes(query, delegate), Response::getMessage);
  }

  public Response handleRes(Message query, Function<Message, Response> delegate) {
    final var key = buildKey(query);
    final var res = this.cache.computeIfAbsent0(key, (k) -> {
      log.trace("status=lookup, key={}, req={}", key, Messages.simplePrint(query));
      final var _res = delegate.apply(query);
      if (_res == null) {
        log.debug("status=noAnswer, action=cantCache, k={}", k);
        return null;
      }
      final var ttl = _res.getTtl();
      log.debug("status=hotload, k={}, ttl={}, simpleMsg={}", k, ttl, Messages.simplePrint(query));
      return Pair.of(_res, ttl);
    });
    if (res == null) {
      return null;
    }
    return res.withMessage(Messages.mergeId(query, res.getMessage()));
  }

  static String buildKey(Message reqMsg) {
    final var type = findQuestionType(reqMsg);
    return String.format("%s-%s", type != null ? type : UUID.randomUUID(), findQuestionHostname(reqMsg));
  }

  public int getSize() {
    return this.cache.getSize();
  }

  public void clear() {
    this.cache.clear();
  }

  public Map<String, CacheEntry> asMap() {
    final var m = this.cache.asMap();
    final var tmpMap = new HashMap<String, CacheEntry>();
    final var keys = new HashSet<>(m.keySet());
    for (final String k : keys) {
      final var v = m.get(k);
      final var entry = new CacheEntry()
        .setKey(k)
        .setTtl(v.getTtl())
        .setCreatedAt(v.getCreatedAt());
      tmpMap.put(k, entry);
    }
    return tmpMap;
  }

  public Name name() {
    return this.name;
  }

}
