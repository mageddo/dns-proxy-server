package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name.GLOBAL;
import static com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name.REMOTE;

@Slf4j
@Singleton
public class SolverCacheFactory {

  private final SolverCache remote;
  private final SolverCache global;

  @Inject
  public SolverCacheFactory(
    @CacheName(name = REMOTE)
    SolverCache remote,

    @CacheName(name = GLOBAL)
    SolverCache global
  ) {
    this.remote = remote;
    this.global = global;
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

  public List<Map<String, CacheEntry>> findCachesAsMap(Name name){
    return this.findInstances(name)
      .stream()
      .map(SolverCache::asMap)
      .toList()
      ;
  }

  private List<SolverCache> getCaches() {
    return List.of(this.remote, this.global);
  }

  public void clear(Name name) {
    if (name == null) {
      for (final var cache : this.getCaches()) {
        cache.clear();
      }
      return;
    }
    this.getInstance(name).clear();
  }

  public List<Integer> findInstancesSize(Name name) {
    return this.findInstances(name)
      .stream()
      .map(SolverCache::getSize)
      .toList()
    ;
  }
}
