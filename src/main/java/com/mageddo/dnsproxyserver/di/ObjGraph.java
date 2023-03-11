package com.mageddo.dnsproxyserver.di;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = MainModule.class)
public interface ObjGraph {
  static ObjGraph create(){
    return DaggerObjGraph.create();
  }
}
