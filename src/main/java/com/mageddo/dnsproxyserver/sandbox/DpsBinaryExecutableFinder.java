package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.utils.Runtime;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.graalvm.nativeimage.ImageInfo;

import java.nio.file.Files;
import java.nio.file.Path;

public class DpsBinaryExecutableFinder {

  public static Path find() {
    return new DpsBinaryExecutableFinder().findBestExecutablePath();
  }

  Path findBestExecutablePath() {
    if (ImageInfo.inImageRuntimeCode()) {
      return findBuiltNativeExecutablePath();
    }
    return findBuiltJarPath();
  }

  Path findBuiltNativeExecutablePath() {
    final var buildPath = this.findBuilPath();
    final var path = buildPath.resolve("native/nativeIntTestCompile/dns-proxy-server-tests");
    Validate.isTrue(Files.exists(path), "Native executable not found at: " + path);
    return path;
  }

  Path findBuiltJarPath() {
    final var buildPath = this.findBuilPath();
    final var libsPath = buildPath.resolve("libs");
    return findFirstMatchInPath(libsPath);
  }

  @SneakyThrows
  private Path findFirstMatchInPath(Path libsPath) {
    try (var stream = Files.list(libsPath)) {
      return stream
        .filter(path -> path.toString().endsWith("-all.jar"))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Unable to find fat jar at libs path"));
    }
  }

  private Path findBuilPath() {
    return Runtime.getRunningDir().getParent().getParent().getParent();
  }
}
