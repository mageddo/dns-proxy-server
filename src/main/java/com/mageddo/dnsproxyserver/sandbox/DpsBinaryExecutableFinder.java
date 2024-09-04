package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.utils.Runtime;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.Validate;
import org.graalvm.nativeimage.ImageInfo;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class DpsBinaryExecutableFinder {

  public static CommandLine buildCommandLine() {
    return new DpsBinaryExecutableFinder().buildCommandLineForBestExecutable();
  }

  private CommandLine buildCommandLineForBestExecutable() {
    final var executablePath = this.findBestExecutablePath();
    if (ImageInfo.inImageRuntimeCode()) {
      return new CommandLine(executablePath.toString());
    }
    final var javaCommandPath = this.findJavaCommand();
    return new CommandLine(javaCommandPath)
      .addArgument("-jar")
      .addArgument(executablePath.toFile().toString());
  }

  private String findJavaCommand() {
    return ProcessHandle.current()
      .info()
      .command()
      .orElseThrow(() -> new IllegalStateException("Couldn't find current java process command"))
      ;
  }

  public static Path findPath() {
    return new DpsBinaryExecutableFinder().findBestExecutablePath();
  }

  Path findBestExecutablePath() {
    if (ImageInfo.inImageRuntimeCode()) {
      return findBuiltNativeExecutablePath();
    }
    return findBuiltJarPath();
  }

  Path findBuiltNativeExecutablePath() {
    final var buildPath = this.findBuildPath();
    final var path = buildPath.resolve("native/nativeIntTestCompile/dns-proxy-server-tests");
    Validate.isTrue(Files.exists(path), "Native executable not found at: " + path);
    return path;
  }

  Path findBuiltJarPath() {
    final var buildPath = this.findBuildPath();
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

  private Path findBuildPath() {
    var path = Runtime.getRunningDir();
    while (path != null) {
      if (path.endsWith("build")) {
        log.trace("buildPath={}", path);
        return path;
      }
      path = path.getParent();
    }
    return null;
  }
}
