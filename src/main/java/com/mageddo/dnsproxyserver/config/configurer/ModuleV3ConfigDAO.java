package com.mageddo.dnsproxyserver.config.configurer;

import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

import com.mageddo.di.InstanceImpl;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.EnvConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.YamlConfigDAO;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module
public interface ModuleV3ConfigDAO {
  @Provides
  static Instance<ConfigDAO> multiSourceConfigDAOInstance(Set<ConfigDAO> instances) {
    return new InstanceImpl<>(instances);
  }

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<ConfigDAO> configDaos(
      EnvConfigDAO o1, JsonConfigDAO o2, YamlConfigDAO o3
  ) {
    return Set.of(o1, o2, o3);
  }

}
