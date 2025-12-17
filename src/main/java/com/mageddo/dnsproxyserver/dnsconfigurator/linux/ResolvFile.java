package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.nio.file.Path;

import lombok.Value;

@Value
public class ResolvFile {

  private Path path;
  private Type type;

  public static ResolvFile of(Path path, Type type) {
    return new ResolvFile(path, type);
  }

  public boolean isResolvconf() {
    return this.type.isResolvconf();
  }

  public boolean isResolved() {
    return this.type.isResolved();
  }

  public enum Type {

    RESOLVCONF,
    SYSTEMD_RESOLVED;

    public boolean isResolvconf() {
      return this == RESOLVCONF;
    }

    public boolean isResolved() {
      return this == SYSTEMD_RESOLVED;
    }
  }

}
