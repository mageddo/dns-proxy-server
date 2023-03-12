package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.Context;

public class App {
  public static void main(String[] args) {

    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();

    // fixme ajustar logs do logback
    AppSettings.setup(Configs.getInstance());

    // todo install as service

  }
}
