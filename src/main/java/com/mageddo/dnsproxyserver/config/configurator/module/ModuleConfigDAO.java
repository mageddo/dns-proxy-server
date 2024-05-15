package com.mageddo.dnsproxyserver.config.configurator.module;

import com.mageddo.di.InstanceImpl;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAOEnv;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAOJson;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleConfigDAO {
  @Provides
  static Instance<MultiSourceConfigDAO> multiSourceConfigDAOInstance(Set<MultiSourceConfigDAO> instances){
    return new InstanceImpl<>(instances);
  }

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<MultiSourceConfigDAO> configDaos(
    MultiSourceConfigDAOEnv o1, MultiSourceConfigDAOCmdArgs o2, MultiSourceConfigDAOJson o3
  ) {
    return Set.of(o1, o2, o3);
  }
}
