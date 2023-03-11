package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.di.InstanceImpl;
import com.mageddo.dnsproxyserver.server.dns.RequestHandler;
import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleMain {

  @Provides
  static Instance<Solver> solversInstance(Set<Solver> solvers){
    return new InstanceImpl<>(solvers);
  }

  @Binds
  @Singleton
  RequestHandler configDAO(RequestHandlerDefault impl);

}
