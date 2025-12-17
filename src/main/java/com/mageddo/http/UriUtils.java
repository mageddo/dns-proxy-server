package com.mageddo.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

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
   *   /do/stuff
   *   /style.css
   *   </pre>
   * </code>
   */
  public static String canonicalPath(String path) {
    if (path.equals(Path.SEPARATOR)) {
      return Path.SEPARATOR;
    }
    if (path.endsWith(Path.SEPARATOR)) {
      return path.substring(0, path.length() - 1);
    }
    return path;
  }

  public static URI createURI(String uri) {
    if (StringUtils.isBlank(uri)) {
      return null;
    }
    return URI.create(uri);
  }
}
