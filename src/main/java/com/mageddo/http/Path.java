package com.mageddo.http;

public class Path {

  public static final String SEPARATOR = "/";

  private final String[] tokens;
  private final String raw;

  Path(String path) {
    this.raw = path;
    this.tokens = path.split(SEPARATOR);
  }

  public static Path of(String path) {
    return new Path(path);
  }

  public static Path of(String root, String... subdirs) {
    final var sb = new StringBuilder(UriUtils.canonicalPath(root));
    for (int i = 0; i < subdirs.length; i++) {
      if (i == 0 && !root.endsWith(SEPARATOR)) {
        sb.append(SEPARATOR);
      }
      final String subdir = subdirs[i];
      final var tmpPath = UriUtils.canonicalPath(subdir);
      sb.append(tmpPath.startsWith(SEPARATOR) ? tmpPath.substring(1) : tmpPath);
      if (i + 1 < subdirs.length) {
        sb.append(SEPARATOR);
      }
    }
    return Path.of(sb.toString());
  }

  public String[] getTokens() {
    return this.tokens;
  }

  public Path getParent() {
    throw new UnsupportedOperationException();
  }

  public int getTokensLength() {
    return this.tokens.length;
  }

  public int indexOf(String wantedToken) {
    for (int i = 0; i < this.tokens.length; i++) {
      final String token = this.tokens[i];
      if (wantedToken.equals(token)) {
        return i;
      }
    }
    return -1;
  }

  public String getRaw() {
    return this.raw;
  }
}
