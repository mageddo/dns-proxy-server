package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.AppSettings;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.di.Context;

public class App {
  public static void main(String[] args) {

    final var config = Configs.getInstance(args);

    new AppSettings().setupLogs(config);

    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();

    // todo install as service

  }
}
