package com.mageddo.dnsproxyserver.solver;

import com.mageddo.concurrent.SingleThreadQueueProcessor;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mageddo.dnsproxyserver.solver.CacheName.Name.GLOBAL;
import static com.mageddo.dnsproxyserver.solver.CacheName.Name.REMOTE;

@Slf4j
@Singleton
public class SolverCacheFactory {

  private final SolverCache remote;
  private final SolverCache global;
  private final SingleThreadQueueProcessor queueProcessor;

  @Inject
  public SolverCacheFactory(
    @CacheName(name = REMOTE)
    SolverCache remote,

    @CacheName(name = GLOBAL)
    SolverCache global
  ) {
    this.remote = remote;
    this.global = global;
    this.queueProcessor = new SingleThreadQueueProcessor();
  }

  public SolverCache getInstance(Name name) {
    return switch (name) {
      case GLOBAL -> this.global;
      case REMOTE -> this.remote;
    };
  }

  public List<SolverCache> findInstances(Name name) {
    if (name == null) {
      return this.getCaches();
    }
    return Collections.singletonList(this.getInstance(name));
  }

  public Map<String, Map<String, CacheEntry>> findCachesAsMap(Name name) {
    return this.findInstances(name)
      .stream()
      .collect(Collectors.toMap(it -> it.name().name(), SolverCache::asMap))
      ;
  }

  private List<SolverCache> getCaches() {
    return List.of(this.remote, this.global);
  }

  public void clear(Name name) {
    if (name == null) {
      this.scheduleCacheClear();
      return;
    }
    this.getInstance(name).clear();
  }

  public Map<String, Integer> findInstancesSizeMap(Name name) {
    return this.findInstances(name)
      .stream()
      .collect(Collectors.toMap(it -> it.name().name(), SolverCache::getSize))
      ;
  }

  /**
   * This method should be called from one single thread, or it can cause deadlock, see #522
   */
  public void scheduleCacheClear() {
    this.queueProcessor.schedule(this::clearCaches);
    log.debug("status=scheduled");
  }

  void clearCaches() {
    for (final var cache : this.getCaches()) {
      log.trace("status=clearing, cache={}", cache.name());
      cache.clear();
      log.trace("status=cleared, cache={}", cache.name());
    }
    log.debug("status=finished, caches={}", this.getCaches().size());
  }

  public int getProcessedInBackground(){
    return this.queueProcessor.getProcessedCount();
  }
}
