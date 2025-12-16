package com.mageddo.dnsproxyserver.docker.application;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.os.Platform;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DockerConnectionCheck {

  public static final Duration DEFAULT_TTL = Duration.ofSeconds(30);

  volatile Status status;
  private final DockerClient client;
  private final Object _lock = new Object();

  public boolean isConnected() {
    if (this.isSupportedPlatform()) {
      if (this.status == null) {
        this.updateStatus();
      } else {
        if (this.hasExpired()) {
          this.triggerUpdate();
        }
      }
      return this.status.isConnected();
    }
    log.trace("docker features still not supported on this platform :/ , hold tight I'm working hard to fix it someday :D");
    return false; // todo support all platforms...
  }

  boolean isSupportedPlatform() {
    return Platform.isLinux() || Platform.isMac() || Platform.isWindows();
  }

  void updateStatus() {
    synchronized (this._lock) {
      final var expired = this.hasExpired();
      final var isNull = this.status == null;
      if (isNull || expired) {
        log.debug("status=updatingDockerStatus, null={}, expired={}", isNull, expired);
        this.status = this.buildStatus();
      }
    }
  }

  void triggerUpdate() {
    ThreadPool
      .main()
      .submit(this::updateStatus);
  }

  private boolean hasExpired() {
    return this.status != null &&
      Duration
        .between(this.status.getCreatedAt(), LocalDateTime.now())
        .compareTo(getTtl()) >= 1;
  }

  static Duration getTtl() {
    return DEFAULT_TTL;
  }

  private Status buildStatus() {
    try {
      this.client.versionCmd().exec();
      return Status.connected();
    } catch (Throwable e) {
      return Status.disconnected();
    }
  }

  @Value
  static class Status {

    private final boolean connected;
    private final LocalDateTime createdAt;

    public static Status connected() {
      return new Status(true, LocalDateTime.now());
    }

    public static Status disconnected() {
      return new Status(false, LocalDateTime.now());
    }
  }
}
