package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurators;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface ModuleStartup {

  @Binds
  @IntoSet
  StartupEvent startupBeans(DnsConfigurators b1);

}
