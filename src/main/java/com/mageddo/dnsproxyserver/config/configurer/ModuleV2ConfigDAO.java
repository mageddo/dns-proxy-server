package com.mageddo.dnsproxyserver.config.configurer;

import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

import com.mageddo.di.InstanceImpl;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.ConfigDAOJson;
import com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv.ConfigDAOLegacyEnv;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module
public interface ModuleV2ConfigDAO {
  @Provides
  static Instance<ConfigDAO> multiSourceConfigDAOInstance(Set<ConfigDAO> instances) {
    return new InstanceImpl<>(instances);
  }

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<ConfigDAO> configDaos(
      ConfigDAOLegacyEnv o1, ConfigDAOCmdArgs o2, ConfigDAOJson o3
  ) {
    return Set.of(o1, o2, o3);
  }

}
