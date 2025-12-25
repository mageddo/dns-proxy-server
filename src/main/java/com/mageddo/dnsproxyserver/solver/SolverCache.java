package com.mageddo.dnsproxyserver.solver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.dns.utils.Messages.findQuestionHostname;
import static com.mageddo.dns.utils.Messages.findQuestionType;

@Slf4j
public class SolverCache {

  private final Name name;
  private final Cache<String, CacheValue> cache;

  public SolverCache(Name name) {
    this.name = name;
    this.cache = Caffeine.newBuilder()
        .maximumSize(2048)
        .expireAfter(buildExpiryPolicy())
        .build();
  }

  public Message handle(Message query, Function<Message, Response> delegate) {
    return Objects.mapOrNull(this.handleRes(query, delegate), Response::getMessage);
  }

  public Response handleRes(Message query, Function<Message, Response> delegate) {
    final var key = buildKey(query);
    final var cachedValue = this.cache.getIfPresent(key);

    if (cachedValue != null) {
      return this.mapResponse(query, cachedValue);
    }

    final var calculatedValue = this.calculateValueWithoutLocks(key, query, delegate);
    this.cacheValue(key, calculatedValue);
    return this.mapResponse(query, calculatedValue);
  }

  void cacheValue(String key, CacheValue calculatedValue) {
    this.cache.get(key, k -> calculatedValue);
  }

  Response mapResponse(Message query, CacheValue cachedValue) {
    if (cachedValue == null) {
      return null;
    }
    final var response = cachedValue.getResponse();
    return response.withMessage(Messages.mergeId(query, response.getMessage()));
  }

  CacheValue calculateValueWithoutLocks(
      String key, Message query, Function<Message, Response> delegate
  ) {
    log.trace("status=lookup, key={}, req={}", key, Messages.simplePrint(query));
    final var _res = delegate.apply(query);
    if (_res == null) {
      log.debug("status=noAnswer, action=cantCache, key={}", key);
      return null;
    }
    final var ttl = _res.getDpsTtl();
    log.debug("status=hotload, k={}, ttl={}, simpleMsg={}", key, ttl, Messages.simplePrint(query));
    return CacheValue.of(_res, ttl);
  }

  static String buildKey(Message reqMsg) {
    final var type = findQuestionType(reqMsg);
    return String.format("%s-%s", type != null ? type : UUID.randomUUID(),
        findQuestionHostname(reqMsg)
    );
  }

  public int getSize() {
    return (int) this.cache.estimatedSize();
  }

  public void clear() {
    this.cache.invalidateAll();
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
          .setExpiresAt(v.getExpiresAt());
      tmpMap.put(k, entry);
    }
    return tmpMap;
  }

  public Name name() {
    return this.name;
  }

  public CacheValue get(String key) {
    return this.cache.getIfPresent(key);
  }

  @Value
  @Builder
  static class CacheValue {

    private Response response;
    private Duration ttl;

    public static CacheValue of(Response res, Duration ttl) {
      return CacheValue
          .builder()
          .response(res)
          .ttl(ttl)
          .build();
    }

    public LocalDateTime getExpiresAt() {
      return this.response
          .getCreatedAt()
          .plus(this.ttl)
          ;
    }
  }

  private static Expiry<String, CacheValue> buildExpiryPolicy() {
    return new Expiry<>() {
      @Override
      public long expireAfterCreate(String key, CacheValue value, long currentTime) {
        return value.getTtl()
            .toNanos();
      }

      @Override
      public long expireAfterUpdate(String key, CacheValue value, long currentTime,
          long currentDuration) {
        return currentDuration;
      }

      @Override
      public long expireAfterRead(String key, CacheValue value, long currentTime,
          long currentDuration) {
        return currentDuration;
      }
    };
  }


}
