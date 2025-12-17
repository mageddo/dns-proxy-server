package com.mageddo.dnsproxyserver.solver.docker.entrypoint;

import java.io.Closeable;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.application.DockerNetworkService;
import com.mageddo.dnsproxyserver.solver.docker.application.DpsDockerEnvironmentSetupService;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventListener implements StartupEvent {

  private final DockerClient dockerClient;
  private final DpsDockerEnvironmentSetupService dpsDockerEnvironmentSetupService;
  private final DockerNetworkService networkService;

  @Override
  public void onStart() {

    if (!this.dpsDockerEnvironmentSetupService.setup()) {
      log.info("status=containerAutoConnectToDpsNetworkDisabled");
      return;
    }

    final var callback = new ResultCallback<Event>() {
      @Override
      public void close() {
      }

      @Override
      public void onStart(Closeable closeable) {
        log.info("status=listeningContainersToConnectToDpsNetwork");
      }

      @Override
      public void onNext(Event event) {
        try {
          log.debug(
              "status=event, id={}, action={}, type={}, status={}, event={}",
              event.getId(), event.getAction(), event.getType(), event.getStatus(), event
          );
          if (StringUtils.equals(event.getAction(), "start")) {
            networkService.connectContainerTo(Network.Name.DPS.lowerCaseName(), event.getId());
            return;
          }
          log.debug("status=eventIgnored, event={}", event);
        } catch (Throwable e) {
          log.warn("status=errorWhenProcessingEvent, msg={}, event={}", e.getMessage(), event, e);
        }
      }

      @Override
      public void onError(Throwable throwable) {
      }

      @Override
      public void onComplete() {
      }
    };
    this.dockerClient
        .eventsCmd()
        .withEventFilter("start")
        .exec(callback);
  }

}
