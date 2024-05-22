package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.AppSettings;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.di.Context;
import org.apache.commons.lang3.BooleanUtils;

public class App {
  public static void main(String[] args) {

    ConfigDAOCmdArgs.setArgs(args);

    final var config = Configs.getInstance();

    checkExitCommands(config);

    new AppSettings().setupLogs(config);

    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();

    // todo install as service
  }

  static void checkExitCommands(Config config) {
    if (BooleanUtils.isTrue(config.isHelpCmd()) || config.isVersionCmd()) {
      System.exit(0);
    }
  }
}
