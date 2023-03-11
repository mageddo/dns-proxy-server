package com.mageddo.http;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UriUtils {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private UriUtils() {
  }

  public static List<NameValuePair> findQueryParams(URI uri) {
    return URLEncodedUtils.parse(uri, DEFAULT_CHARSET);
  }

  public static String canonicalPath(URI uri) {
    final var path = uri.getPath();
    return canonicalPath(path);
  }

  /**
   * Examples:
   * <code>
   * <pre>
   *   /
   *   /a/
   *   /abc/
   *   </pre>
   * </code>
   */
  public static String canonicalPath(String path) {
    if (path.endsWith("/")) {
      return path;
    }
    return path + "/";
  }
}
