package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinuxResolverConfDetector {
  public static ResolvFile.Type build(Path path) {
    try {
      if (Files.size(path) == 0) {
        final var fileName = path.getFileName().toString();
        if (fileName.equals("resolv.conf")) {
          return ResolvFile.Type.RESOLVCONF;
        } else if (fileName.equals("resolved.conf")) {
          return ResolvFile.Type.SYSTEMD_RESOLVED;
        }
      }
      return null;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
