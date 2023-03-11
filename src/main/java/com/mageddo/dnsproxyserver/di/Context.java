package com.mageddo.dnsproxyserver.di;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = MainModule.class)
public interface Context {
  static Context create(){
    return DaggerObjGraph.create();
  }
}
