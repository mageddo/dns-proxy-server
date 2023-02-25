package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import lombok.Value;

import java.nio.file.Path;

@Value
public class ResolvFile {

  private Path path;
  private Type type;

  public static ResolvFile of(Path path, Type type) {
    return new ResolvFile(path, type);
  }

  public enum Type {
    RESOLVCONF,
    SYSTEMD_RESOLVED
  }

}
