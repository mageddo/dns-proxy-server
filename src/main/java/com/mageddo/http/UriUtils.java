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

  public static List<NameValuePair> findQueryParams(URI uri){
    return URLEncodedUtils.parse(uri, DEFAULT_CHARSET);
  }
}
