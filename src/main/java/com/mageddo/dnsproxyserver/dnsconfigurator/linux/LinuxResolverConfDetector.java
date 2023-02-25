package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ResolvFile.Type;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinuxResolverConfDetector {
  public static Type detect(Path path) {

    if (isSystemdResolved(path)) {
      return Type.SYSTEMD_RESOLVED;
    } else if (isResolvConf(path)) {
      return Type.RESOLVCONF;
    }

    final var fileName = path.getFileName().toString();
    if (fileName.equals("resolv.conf")) {
      return Type.RESOLVCONF;
    } else if (fileName.equals("resolved.conf")) {
      return Type.SYSTEMD_RESOLVED;
    }
    return null;
  }

  static boolean isResolvConf(Path path) {
    try {
      for (String line : Files.readAllLines(path)) {
        if (line.startsWith("nameserver ")) {
          return true;
        }
      }
      return false;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static boolean isSystemdResolved(Path path) {
    boolean header = false, dnsOption = false;
    try {
      for (String line : Files.readAllLines(path)) {
        if (line.startsWith("[Resolve]")) {
          header = true;
        }
        if (line.startsWith("DNS=")) {
          dnsOption = true;
        }
        if (header && dnsOption) {
          return true;
        }
      }
      return false;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
