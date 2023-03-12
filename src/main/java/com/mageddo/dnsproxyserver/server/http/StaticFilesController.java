package com.mageddo.dnsproxyserver.server.http;

import com.mageddo.http.HttpMapper;
import com.mageddo.http.WebServer;
import com.sun.net.httpserver.SimpleFileServer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class StaticFilesController implements HttpMapper {

  @Inject
  public StaticFilesController() {
  }

  @Override
  public void map(WebServer server) {
    server.map("/static", SimpleFileServer.createFileHandler(buildStaticResourcesPath()));
  }

  // fixme must copy static files to a temp dir when running inside a jar.
  static Path buildStaticResourcesPath() {
    return Path.of("/tmp");
  }

}
