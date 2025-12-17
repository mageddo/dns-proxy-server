package com.mageddo.dnsproxyserver.server.http;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.io.IoUtils;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.HttpStatus;
import com.mageddo.http.WebServer;
import com.sun.net.httpserver.SimpleFileServer;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class StaticFilesController implements HttpMapper {

  private Path tmpDir;
  private volatile boolean loaded = false;

  @Inject
  public StaticFilesController() {
  }

  @Override
  public void map(WebServer server) {

    server.get("/", exchange -> {
          exchange
              .getResponseHeaders()
              .add("Location", "/static");
          exchange.sendResponseHeaders(HttpStatus.MOVED_PERMANENTLY, -1);
        }
    );

    final var handler = SimpleFileServer.createFileHandler(this.createServePath());
    server.map("/static/.*", exchange -> {
          try {
            if (this.loaded) {
              return;
            }
            synchronized (this) {
              if (this.loaded) {
                return;
              }
              final var staticFilesArchive = "/META-INF/resources/static.tgz";
              final var gzip = IoUtils.getResourceAsStream(staticFilesArchive);
              if (gzip == null) {
                log.info("status=noStaticFilesArchiveFound, archive={}", staticFilesArchive);
                this.loaded = true;
                return;
              }
              final var archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR,
                  CompressionType.GZIP
              );
              archiver.extract(gzip, this.tmpDir.toFile());
              this.loaded = true;
              log.debug("status=staticFilesExtracted, path={}", this.tmpDir);
              return;
            }
          } catch (Throwable e) {
            log.warn("status=failedOnMountStaticFiles, msg={}", e.getMessage());
            this.loaded = true;
          } finally {
            handler.handle(exchange);
          }
        }
    );
  }

  Path createServePath() {
    try {
      this.tmpDir = Files.createTempDirectory("dps-static-");
      Runtime
          .getRuntime()
          .addShutdownHook(new Thread(() -> {
            try {
              FileUtils.deleteDirectory(tmpDir.toFile());
            } catch (Throwable e) {
            }
          }));
      return this.tmpDir;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
