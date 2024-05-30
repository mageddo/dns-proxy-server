package com.mageddo.dnsproxyserver.solver;

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
      for (final var cache : this.getCaches()) {
        cache.clear();
      }
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
}
