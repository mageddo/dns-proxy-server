package com.mageddo.dnsproxyserver.di.module;

import java.util.Set;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.server.http.StaticFilesController;
import com.mageddo.dnsproxyserver.server.rest.CacheResource;
import com.mageddo.dnsproxyserver.server.rest.EnvResource;
import com.mageddo.dnsproxyserver.server.rest.HostnameResource;
import com.mageddo.dnsproxyserver.server.rest.NetworkResource;
import com.mageddo.http.HttpMapper;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module
public interface ModuleHttpMapper {

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<HttpMapper> mappers(
      CacheResource o1,
      HostnameResource o2,
      NetworkResource o3,
      EnvResource o4,
      StaticFilesController o5
  ) {
    return Set.of(
        o1,
        o2,
        o3,
        o4,
        o5
    );
  }

}
