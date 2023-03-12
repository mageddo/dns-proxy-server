package com.mageddo.utils.dagger;

import com.mageddo.dnsproxyserver.di.Context;


//@Singleton
//@Component(
//  modules = {
//    ModuleMain.class,
//    ModuleDockerClient.class,
//    QuarkusConfig.class,
//    ModuleHttpMapper.class,
//    ModuleSolver.class,
//    ModuleStartup.class,
//    ModuleMap.class,
//    // mocks
//    ModuleDaoSpy.class,
//  }
//)
@Deprecated(forRemoval = true)
interface TestContext extends Context {
  static TestContext create() {
//    return DaggerTestContext.create();
    return null;
  }

}
