package com.mageddo.utils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.ObjectUtils;
import org.graalvm.nativeimage.ImageInfo;
import org.graalvm.nativeimage.ProcessProperties;

import lombok.SneakyThrows;

public class Runtime {

  /**
   * @return the Jar path when running into a jar or null if not
   */
  @SneakyThrows
  public static Path getRunningJar() {
    final var jarPath = getRunningPath();
    return isJar(jarPath) ? jarPath : null;
  }

  /**
   * @return A directory when running on classes on directory
   * or a jar when running into a jar
   */
  @SneakyThrows
  public static Path getRunningPath() {
    final var url = getRunningURL();
    return Paths.get(url.toURI());
  }

  public static Path getRunningDir() {
    if (ImageInfo.inImageRuntimeCode()) {
      return Paths.get(ProcessProperties.getExecutableName())
          .getParent();
    }
    final var path = getRunningPath();
    if (isJar(path)) {
      return path.getParent();
    }
    return path;
  }

  public static boolean runningOnJar() {
    return isJar(getRunningPath());
  }

  public static boolean isJar(Path jarPath) {
    return jarPath.toString()
        .endsWith(".jar");
  }

  private static URL getRunningURL() {
    return ObjectUtils.firstNonNull(
        getUrlWhenNotJar(),
        getUrlWhenJarAndNotJar()
    );
  }

  private static URL getUrlWhenNotJar() {
    return Runtime.class
        .getClassLoader()
        .getResource(".");
  }

  private static URL getUrlWhenJarAndNotJar() {
    return Runtime.class
        .getProtectionDomain()
        .getCodeSource()
        .getLocation();
  }
}
