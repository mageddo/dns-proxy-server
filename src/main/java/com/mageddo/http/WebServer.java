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
import java.util.Set;

@Slf4j
public class WebServer {

  private final Set<HttpMapper> mappers;
  private HttpServer server;

  @Inject
  public WebServer(Set<HttpMapper> mappers) {
    this.mappers = mappers;
  }

  public WebServer map(String path, HttpHandler handler) {
    this.server.createContext(path, handler);
    return this;
  }

  public void start(int port) {
    try {
      this.server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/static", SimpleFileServer.createFileHandler(buildStaticResourcesPath()));
      this.mappers.forEach(it -> it.handle(this));
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
