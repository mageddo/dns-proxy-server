package com.mageddo.utils.dagger;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.di.module.ModuleDockerClient;
import com.mageddo.dnsproxyserver.di.module.ModuleHttpMapper;
import com.mageddo.dnsproxyserver.di.module.ModuleMain;
import com.mageddo.dnsproxyserver.di.module.ModuleSolver;
import com.mageddo.dnsproxyserver.di.module.ModuleStartup;
import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import dagger.Component;

import javax.inject.Singleton;


@Singleton
@Component(
  modules = {
    ModuleMain.class,
    ModuleDockerClient.class,
    QuarkusConfig.class,
    ModuleHttpMapper.class,
    ModuleSolver.class,
    ModuleStartup.class,
    // mocks
    ModuleDaoSpy.class,
  }
)
public interface TestContext extends Context {
  static TestContext create() {
    return DaggerTestContext.create();
  }

}
