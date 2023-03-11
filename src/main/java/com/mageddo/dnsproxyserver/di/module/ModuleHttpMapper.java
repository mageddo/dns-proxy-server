package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.server.rest.CacheResource;
import com.mageddo.dnsproxyserver.server.rest.HostnameResource;
import com.mageddo.http.HttpMapper;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleHttpMapper {

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<HttpMapper> mappers(CacheResource o1, HostnameResource o2) {
    return Set.of(
      o1,
      o2
    );
  }

}
