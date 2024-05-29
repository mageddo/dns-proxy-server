package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsserver.RequestHandler;
import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface ModuleMain {

  @Binds
  @Singleton
  RequestHandler requestHandler(RequestHandlerDefault impl);

}
