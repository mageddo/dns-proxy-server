package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * See
 *  https://dagger.dev/dev-guide/multibindings
 *  https://stackoverflow.com/questions/62150127/is-it-possible-to-get-beans-by-class-type-in-dagger2-similarly-to-spring-does
 */
@Module
public interface ModuleMap {

  @Binds
  @IntoMap
  @ClassKey(DockerDAO.class)
  Object b1(DockerDAO bean);


  @Binds
  @IntoMap
  @ClassKey(ContainerSolvingService.class)
  Object b2(ContainerSolvingService bean);
}
