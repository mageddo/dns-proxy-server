package com.mageddo.dnsproxyserver.solver.cache;

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
import com.mageddo.dnsproxyserver.solver.NamedResponse;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.dns.utils.Messages.findQuestionHostname;
import static com.mageddo.dns.utils.Messages.findQuestionType;
import static com.mageddo.dns.utils.Messages.simplePrint;

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

  public Message handleToMsg(Message query, Function<Message, NamedResponse> delegate) {
    return Objects.mapOrNull(this.handle(query, delegate), Value::getMessage);
  }

  public Value handle(Message query, Function<Message, NamedResponse> delegate) {

    final var key = buildKey(query);

    final var cachedValue = this.cache.getIfPresent(key);
    if (cachedValue != null) {
      return Value.builder()
          .hotload(false)
          .response(this.mapResponse(query, cachedValue, false))
          .build();
    }

    final var calculatedValue = this.calcCacheAndGet(key, query, delegate);
    if (calculatedValue == null) {
      return null;
    }
    return Value.builder()
        .hotload(true)
        .response(this.mapResponse(query, calculatedValue, true))
        .build();
  }

  /**
   * Same k can be queried twice because no lock is done when doing the query.
   * This is done to prevent deadlocks.
   */
  CacheValue calcCacheAndGet(String key, Message query, Function<Message, NamedResponse> delegate) {
    final var value = this.calc(key, query, delegate);
    if (value == null) {
      return null;
    }
    this.cacheValue(key, value);
    return value;
  }

  void cacheValue(String key, CacheValue value) {
    this.cache.get(key, k -> value);
  }

  NamedResponse mapResponse(Message query, CacheValue value, boolean hotload) {
    if (value == null) {
      return null;
    }
    final var response = value.getResponse();
    if (log.isTraceEnabled()) {
      log.trace(
          "status=found, fromCache={}, solver={}, reqRes={}, cacheTtl={}",
          !hotload, response.getSolver(),
          simplePrint(response.getMessage()), value.getTtlAsSeconds()
      );
    }
    return response.withMessage(Messages.mergeId(query, response.getMessage()));
  }

  CacheValue calc(
      String key, Message query, Function<Message, NamedResponse> delegate
  ) {
    final var res = delegate.apply(query);
    if (res == null || res.hasResponse()) {
      if (log.isTraceEnabled()) {
        log.trace("status=nullRes, action=wontCache, k={}", key);
      }
      return null;
    }
    final var ttl = res.getDpsTtl();
    if (log.isTraceEnabled()) {
      final var queryText = Messages.simplePrint(query);
      log.trace("status=res, k={}, ttl={}, simpleMsg={}", key, ttl, queryText);
    }
    return CacheValue.of(res, ttl);
  }

  static String buildKey(Message reqMsg) {
    final var type = findQuestionType(reqMsg);
    return String.format(
        "%s-%s",
        type != null ? type : UUID.randomUUID(),
        // FIXME EFS it makes sense to save random id on cache
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

  @Builder
  @lombok.Value
  static class CacheValue {

    NamedResponse response;
    Duration ttl;

    public static CacheValue of(NamedResponse res, Duration ttl) {
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

    public Long getTtlAsSeconds() {
      return this.ttl.toSeconds();
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
