package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.LogSettings;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.di.Context;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

  private final String[] args;
  private Config config;

  public App(String[] args) {
    this.args = args;
  }

  public static void main(String[] args) {
    new App(args).start();
  }

  void start() {

    this.checkExitCommands();

    this.config = this.findConfig(args);

    this.setupLogs();

    this.setup();

    this.startContext();

    // todo install as service
  }

  void setup() {
    this.createTmpDirIfNotExists();
  }

  @SneakyThrows
  void createTmpDirIfNotExists() {
    final var tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
    Files.createDirectories(tmpDir);
  }

  Config findConfig(String[] args) {
    ConfigDAOCmdArgs.setArgs(args);
    return Configs.getInstance();
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
    final var flags = ConfigFlag.parse(this.args);
    if (flags.isHelp() || flags.isVersion()) {
      exitGracefully();
    }
  }

  void exitGracefully() {
    System.exit(0);
  }
}
