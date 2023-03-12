package com.mageddo.dnsproxyserver.di.module;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Iface2Impl implements Iface2 {

  private final Iface iface;

  @Inject
  public Iface2Impl(Iface iface) {
    this.iface = iface;
  }
}
