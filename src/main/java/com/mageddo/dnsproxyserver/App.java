package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.LogSettings;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.di.Context;
import org.apache.commons.lang3.BooleanUtils;

public class App {

  private Config config;

  public static void main(String[] args) {
    new App().doMain(args);
  }

  void doMain(String[] args) {

    ConfigDAOCmdArgs.setArgs(args);

    this.config = Configs.getInstance();

    this.checkExitCommands();

    this.setupLogs();

    this.startContext();

    // todo install as service
  }

  void setupLogs() {
    new LogSettings().setupLogs(this.config);
  }

  void startContext() {
    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();
  }

  void checkExitCommands() {
    if (BooleanUtils.isTrue(this.config.isHelpCmd()) || this.config.isVersionCmd()) {
      exitGracefully();
    }
  }

  void exitGracefully() {
    System.exit(0);
  }
}
