package com.mageddo.dnsproxyserver.server.dns.solver.docker.entrypoint;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.ContainerService;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.DockerNetworkService;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.DpsDockerEnvironmentSetupService;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Closeable;


@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EventListener implements StartupEvent {

  private final DockerClient dockerClient;
  private final DockerDAO dockerDAO;
  private final DpsDockerEnvironmentSetupService dpsDockerEnvironmentSetupService;
  private final DockerNetworkFacade dockerNetworkDAO;
  private final DockerNetworkService networkService;
  private final ContainerService containerService;

  @Override
  public void onStart() {

    // tag:newMethod todo this code is responsability of an application service
    final var dockerConnected = this.dockerDAO.isConnected();
    log.info("status=binding-docker-events, dockerConnected={}", dockerConnected);
    if (!dockerConnected) {
      return;
    }

    this.dpsDockerEnvironmentSetupService.setup();
    final var config = Configs.getInstance();
    if (!config.getMustConfigureDpsNetwork() || !config.getDpsNetworkAutoConnect()) {
      log.info(
        "status=autoConnectDpsNetworkDisabled, dpsNetwork={}, dpsNetworkAutoConnect={}",
        config.getMustConfigureDpsNetwork(), config.getDpsNetworkAutoConnect()
      );
      return;
    }
    this.containerService.connectRunningContainers();
    // end:newMethod

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
            networkService.connectContainerTo(Network.Name.DPS.lowerCaseName(), event.getId());
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
