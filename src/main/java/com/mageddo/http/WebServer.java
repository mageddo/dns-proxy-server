package com.mageddo.http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

public class WebServer {

  private final List<HttpMapper> mappers;
  private HttpServer server;

  @Inject
  public WebServer(List<HttpMapper> mappers) {
    this.mappers = mappers;
    this.setup(8080);
  }

  public WebServer map(String path, HttpHandler handler) {
    this.server.createContext(path, handler);
    return this;
  }

  void setup(int port) {
    try {
      this.server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/static", SimpleFileServer.createFileHandler(buildStaticResourcesPath()));
      this.mappers.forEach(it -> it.handle(this));
      server.setExecutor(null);
      server.start();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static Path buildStaticResourcesPath() {
    return Path.of("/tmp");
  }

}
