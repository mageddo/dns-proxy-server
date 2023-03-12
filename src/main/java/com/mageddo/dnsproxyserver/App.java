package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.Context;

public class App {
  public static void main(String[] args) {

    AppSettings.setup(Configs.getInstance());

    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();

    // todo install as service

  }
}
