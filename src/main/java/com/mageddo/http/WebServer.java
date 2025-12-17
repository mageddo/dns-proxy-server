package com.mageddo.http;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.mageddo.commons.io.IoUtils;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.commons.lang3.ClassUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebServer implements AutoCloseable {

  public static final String ALL_METHODS_WILDCARD = "";

  static final String DEFAULT_RES_BODY = """
      <h1>404 Not Found</h1>No context found for request""";

  private final byte[] DEFAULT_RES_BODY_BYTES = DEFAULT_RES_BODY.getBytes(UriUtils.DEFAULT_CHARSET);

  private static final String ROOT = "/";

  private final Set<HttpMapper> mappers;
  private final Map<String, Map<String, HttpHandler>> handlesStore = new HashMap<>();
  private HttpServer server;

  public WebServer(HttpMapper... mappers) {
    this(Stream.of(mappers)
        .collect(Collectors.toSet()));
  }

  @Inject
  public WebServer(Set<HttpMapper> mappers) {
    this.mappers = mappers;
  }

  public WebServer get(String path, HttpHandler handler) {
    return this.map(HttpMethod.GET, path, handler);
  }

  public WebServer post(String path, HttpHandler handler) {
    return this.map(HttpMethod.POST, path, handler);
  }

  public WebServer put(String path, HttpHandler handler) {
    return this.map(HttpMethod.PUT, path, handler);
  }

  public WebServer delete(String path, HttpHandler handler) {
    return this.map(HttpMethod.DELETE, path, handler);
  }

  public WebServer head(String path, HttpHandler handler) {
    return this.map(HttpMethod.HEAD, path, handler);
  }

  public WebServer patch(String path, HttpHandler handler) {
    return this.map(HttpMethod.PATCH, path, handler);
  }

  public WebServer map(String method_, String path, HttpHandler handler) {
    final var method = method_ == null ? ALL_METHODS_WILDCARD : method_.toUpperCase(Locale.ENGLISH);
    this.handlesStore.compute(UriUtils.canonicalPath(path), (key, value) -> {
          if (value == null) {
            final var collection = new HashMap<String, HttpHandler>();
            collection.put(method, handler);
            return collection;
          } else {
            value.put(method, handler);
            return value;
          }
        }
    );
    return this;
  }

  public WebServer map(String path, HttpHandler handler) {
    this.map(null, path, handler);
    return this;
  }

  public void start(int port) {
    try {

      this.server = HttpServer.create(new InetSocketAddress(port), 0);

      // load application mappers to the maping store
      this.mappers.forEach(mapper -> mapper.map(this));

      this.server.createContext(ROOT, exchange -> {
            try {
              final var canonicalPath = UriUtils.canonicalPath(exchange.getRequestURI());
              final var handler = this.lookupForHandler(canonicalPath);
              if (handler == null) {
                exchange.sendResponseHeaders(HttpStatus.NOT_FOUND, DEFAULT_RES_BODY_BYTES.length);
                exchange.getResponseBody()
                    .write(DEFAULT_RES_BODY_BYTES);
                return;
              }

              handler
                  .getOrDefault(exchange.getRequestMethod(), pExchange -> {
                        final var defaultPathHandler = handler.get(ALL_METHODS_WILDCARD);
                        if (defaultPathHandler != null) {
                          defaultPathHandler.handle(pExchange);
                        } else {
                          pExchange.sendResponseHeaders(HttpStatus.METHOD_NOT_ALLOWED, 0);
                        }
                      }
                  )
                  .handle(exchange);
            } catch (Exception e) {
              log.error("status=handleFailed, msg={}:{}", ClassUtils.getSimpleName(e),
                  e.getMessage(),
                  e
              );
            } finally {
              try {
                final var responseCode = exchange.getResponseCode();
                if (responseCode == -1) {
                  exchange.sendResponseHeaders(HttpStatus.NO_CONTENT, -1);
                }
                exchange.close();
              } catch (Throwable e) {
                log.warn("status=could not generate default ok response, msg={}", e.getMessage());
              }
            }
          }
      );

      server.setExecutor(null);
      server.start();
      log.info("status=startingWebServer, port={}", port);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Map<String, HttpHandler> lookupForHandler(String canonicalPath) {
    if (this.handlesStore.containsKey(canonicalPath)) {
      return this.handlesStore.get(canonicalPath);
    } else {
      final var wildcardMapPath = Path.of(canonicalPath, Wildcards.ALL_SUB_PATHS_WILDCARD)
          .getRaw();
      if (this.handlesStore.containsKey(wildcardMapPath)) {
        return this.handlesStore.get(wildcardMapPath);
      }
    }
    final var mapPath = Wildcards.findMatchingMap(this.handlesStore.keySet(), canonicalPath);
    return this.handlesStore.get(mapPath);
  }

  public void stop() {
    IoUtils.silentClose(() -> {
      this.server.stop(1);
    });
  }

  @Override
  public void close() {
    this.stop();
  }
}
