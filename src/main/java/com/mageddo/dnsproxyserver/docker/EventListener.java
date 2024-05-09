package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Closeable;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerSolvingAdapter.NETWORK_DPS;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EventListener implements StartupEvent {

  private final DockerClient dockerClient;
  private final DockerFacade dockerFacade;
  private final DpsContainerManager dpsContainerManager;
  private final DockerNetworkFacade dockerNetworkDAO;
  private final DockerNetworkService networkService;

  @Override
  public void onStart() {

    final var dockerConnected = this.dockerFacade.isConnected();
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
        try {
          log.debug(
            "status=event, id={}, action={}, type={}, status={}, event={}",
            event.getId(), event.getAction(), event.getType(), event.getStatus(), event
          );
          if (StringUtils.equals(event.getAction(), "start")) {
            networkService.connect(NETWORK_DPS, event.getId());
            return;
          }
          log.debug("status=eventIgnored, event={}", event);
        } catch (Throwable e){
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
//      .withEventFilter("start", "die", "stop", "destroy")
      .withEventFilter("start")
      .exec(callback);
  }

}
