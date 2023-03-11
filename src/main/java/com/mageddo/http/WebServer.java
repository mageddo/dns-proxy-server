package com.mageddo.http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
public class WebServer {

  private final Set<HttpMapper> mappers;
  private final Map<String, Map<String, HttpHandler>> handlesStore = new HashMap<>();
  private HttpServer server;

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
    final var method = method_.toUpperCase(Locale.ENGLISH);
    this.handlesStore.compute(path, (key, value) -> {
      if (value == null) {
        final var collection = new HashMap<String, HttpHandler>();
        collection.put(method, handler);
        return collection;
      } else {
        value.put(method, handler);
        return value;
      }
    });
    return this;
  }

  public WebServer map(String path, HttpHandler handler) {
    this.server.createContext(path, handler);
    return this;
  }

  public void start(int port) {
    try {
      this.server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/static", SimpleFileServer.createFileHandler(buildStaticResourcesPath()));
      this.mappers.forEach(mapper -> {

        mapper.map(this);

        this.handlesStore.forEach((path, pairs) -> {
          this.server.createContext(path, exchange -> {
            pairs.getOrDefault(exchange.getRequestMethod(), pExchange -> pExchange.sendResponseHeaders(415, 0))
                .handle(exchange);
          });
        });


      });
      server.setExecutor(null);
      server.start();
      log.info("status=startingWebServer, port={}", port);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static Path buildStaticResourcesPath() {
    return Path.of("/tmp");
  }

}
