package com.mageddo.http;

public class Path {

  private final String[] tokens;
  private final String raw;

  Path(String path) {
    this.raw = path;
    this.tokens = path.split("/");
  }

  public static Path of(String path) {
    return new Path(path);
  }

  public String[] getTokens() {
    return this.tokens;
  }

  public Path getParent(){
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
