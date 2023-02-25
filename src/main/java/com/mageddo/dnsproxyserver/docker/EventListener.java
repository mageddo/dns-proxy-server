package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.mageddo.dnsproxyserver.config.Configs;
import io.quarkus.runtime.StartupEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Closeable;

import static com.mageddo.dnsproxyserver.docker.ContainerSolvingService.NETWORK_DPS;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EventListener {

  private final DockerClient dockerClient;
  private final DockerDAO dockerDAO;
  private final DpsContainerManager dpsContainerManager;
  private final DockerNetworkDAO dockerNetworkDAO;
  private final DockerNetworkService networkService;

  void onStart(@Observes StartupEvent ev) {

    final var dockerConnected = this.dockerDAO.isConnected();
    log.info("status=binding-docker-events, dockerConnected={}", dockerConnected);
    if (!dockerConnected) {
      return;
    }

    this.dpsContainerManager.setupNetwork();
    final var config = Configs.getInstance();
    if (!config.getDpsNetwork() || !config.getDpsNetworkAutoConnect()) {
      log.info(
        "status=autoConnectDpsNetworkDisabled, dpsNetwork={}, dpsNetworkAutoConnect={}",
        config.getDpsNetwork(), config.getDpsNetworkAutoConnect()
      );
      return;
    }
    this.dockerNetworkDAO.connectRunningContainers(NETWORK_DPS, DpsContainerManager::isNotDpsContainer);

    final var callback = new ResultCallback<Event>() {
      @Override
      public void close() {
      }

      @Override
      public void onStart(Closeable closeable) {
      }

      @Override
      public void onNext(Event event) {
        log.debug(
          "status=event, id={}, action={}, type={}, status={}, event={}",
          event.getId(), event.getAction(), event.getType(), event.getStatus(), event
        );
        if (StringUtils.equals(event.getAction(), "start")) {
          networkService.connect(NETWORK_DPS, event.getId());
          return;
        }
        log.debug("status=eventIgnore, event={}", event);
      }

      @Override
      public void onError(Throwable throwable) {
      }

      @Override
      public void onComplete() {
      }
    };
    dockerClient
      .eventsCmd()
//      .withEventFilter("start", "die", "stop", "destroy")
      .withEventFilter("start")
      .exec(callback);
  }

}
