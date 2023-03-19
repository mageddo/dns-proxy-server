package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.solver.SolverCacheQualifier.Name;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

import static com.mageddo.dnsproxyserver.server.dns.solver.SolverCacheQualifier.Name.GLOBAL;
import static com.mageddo.dnsproxyserver.server.dns.solver.SolverCacheQualifier.Name.REMOTE;

@Slf4j
@Singleton
public class SolverCacheFactory {

  @SolverCacheQualifier(name = REMOTE)
  private SolversCache remote;

  @SolverCacheQualifier(name = GLOBAL)
  private SolversCache global;

  public SolversCache getInstance(Name name) {
    return switch (name) {
      case GLOBAL -> this.global;
      case REMOTE -> this.remote;
    };
  }

  public List<SolversCache> findInstances(Name name) {
    if (name == null) {
      return this.getCaches();
    }
    return Collections.singletonList(this.getInstance(name));
  }

  private List<SolversCache> getCaches() {
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
      .map(SolversCache::getSize)
      .toList()
    ;
  }
}
