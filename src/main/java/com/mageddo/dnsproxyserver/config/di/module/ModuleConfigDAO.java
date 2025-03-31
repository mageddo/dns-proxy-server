package com.mageddo.dnsproxyserver.config.di.module;

import com.mageddo.di.InstanceImpl;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.VersionDAO;
import com.mageddo.dnsproxyserver.config.provider.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.provider.legacyenv.ConfigDAOLegacyEnv;
import com.mageddo.dnsproxyserver.config.provider.jsonv1v2.dataprovider.ConfigDAOJson;
import com.mageddo.dnsproxyserver.config.provider.prop.VersionDAOProp;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleConfigDAO {
  @Provides
  static Instance<ConfigDAO> multiSourceConfigDAOInstance(Set<ConfigDAO> instances){
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

  @Binds
  @Singleton
  VersionDAO versionDAO(VersionDAOProp impl);
}
