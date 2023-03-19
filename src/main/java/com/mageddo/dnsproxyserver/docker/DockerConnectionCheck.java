package com.mageddo.dnsproxyserver.docker;

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
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerConnectionCheck {

  public static final Duration DEFAULT_TTL = Duration.ofSeconds(30);

  private final DockerClient client;
  private volatile Status status;
  private final Object _lock = new Object();

  public boolean isConnected() {
    if (Platform.isLinux() || Platform.isMac()) {
      if (this.status == null) {
        this.updateStatus();
      }
      return this.status.isConnected();
    }
    this.triggerUpdate();
    log.trace("docker features still not supported on this platform :/ , hold tight I'm working hard to fix it someday :D");
    return false; // todo support all platforms...
  }

  private void updateStatus() {
    synchronized (this._lock) {
      final var expired = this.hasExpired();
      final var isNull = this.status == null;
      if (isNull || expired) {
        log.debug("status=updatingDockerStatus, null={}, expired={}", isNull, expired);
        this.status = this.buildStatus();
      }
    }
  }

  private void triggerUpdate() {
    ThreadPool
      .main()
      .submit(this::updateStatus);
  }

  private boolean hasExpired() {
    return this.status != null &&
      Duration
        .between(this.status.getCreatedAt(), LocalDateTime.now())
        .compareTo(DEFAULT_TTL) >= 1;
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
  private static class Status {

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
