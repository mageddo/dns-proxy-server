package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.LogSettings;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.di.Context;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class App {

  private final String[] args;
  private Config config;
  private ConfigFlag flags;

  public App(String[] args) {
    this.args = args;
  }

  public static void main(String[] args) {
    new App(args).start();
  }

  void start() {
    try {
      log.trace("status=starting");
      this.mustStart();
    } catch (SystemExitException e) {
      throw e;
    } catch (Throwable e) {
      log.error(
        "status=fatalError, action=exit, msg={}, class={}",
        ExceptionUtils.getMessage(e), ClassUtils.getSimpleName(e), e
      );
      this.exitWithError(128);
    }
  }

  void mustStart() {
    this.flags = ConfigFlag.parse(this.args);

    this.checkHiddenCommands();

    this.checkExitCommands();

    this.config = this.findConfig(args);

    this.setupLogs();

    this.startContext();

    // todo install as service
  }

  void checkHiddenCommands() {
    if (this.flags.isCreateTmpDir()) {
      this.createTmpDirIfNotExists();
    }
    log.trace("status=checked");
  }

  Config findConfig(String[] args) {
    ConfigDAOCmdArgs.setArgs(args);
    return Configs.getInstance();
  }

  void setupLogs() {
    log.trace("status=configuring");
    new LogSettings().setupLogs(this.config);
    log.trace("status=configured");
  }

  void startContext() {
    final var context = Context.create();

    // start webserver
    // start dns server
    context.start();
  }

  void checkExitCommands() {
    if (flags.isHelp() || flags.isVersion()) {
      exitGracefully();
    }
    log.trace("status=checked");
  }

  void exitGracefully() {
    System.exit(0);
  }

  void exitWithError(int errorCode) {
    System.exit(errorCode);
  }

  @SneakyThrows
  void createTmpDirIfNotExists() {
    final var tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
    Files.createDirectories(tmpDir);
  }

  Config getConfig() {
    return config;
  }

  int getDnsServerPort() {
    return getConfig().getDnsServerPort();
  }

  static class SystemExitException extends RuntimeException {
    public SystemExitException(String reason) {
      super(reason);
    }
  }
}
