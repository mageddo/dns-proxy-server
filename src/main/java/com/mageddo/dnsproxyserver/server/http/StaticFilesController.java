package com.mageddo.dnsproxyserver.server.http;

import com.mageddo.commons.io.IoUtils;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.WebServer;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.extern.slf4j.Slf4j;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    final var handler = SimpleFileServer.createFileHandler(this.createServePath());
    server.map("/static", exchange -> { // fixme /static e todos os subdirs precisam direcionar pra cá
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
          final var archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
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
    });
  }

  Path createServePath() {
    try {
      this.tmpDir = Files.createTempDirectory("dps-static-");
      this.tmpDir.toFile().deleteOnExit();
      return this.tmpDir;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
