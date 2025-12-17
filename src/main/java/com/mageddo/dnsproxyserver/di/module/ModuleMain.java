package com.mageddo.dnsproxyserver.di.module;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.mageddo.dnsserver.RequestHandler;

import dagger.Binds;
import dagger.Module;

@Module
public interface ModuleMain {

  @Binds
  @Singleton
  RequestHandler requestHandler(RequestHandlerDefault impl);

}
