package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerCompactMapper;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.NetworkMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.function.Predicate;

import static com.mageddo.commons.lang.Objects.mapOrNull;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class NetworkDAODefault implements NetworkDAO {

  private final DockerClient dockerClient;
  private final DockerNetworkFacade dockerNetworkFacade;

  @Override
  public Network findById(String networkId) {
    return NetworkMapper.of(this.dockerNetworkFacade.findById(networkId));
  }

  @Override
  public Network findByName(String networkName) {
    return NetworkMapper.of(this.dockerNetworkFacade.findByName(networkName));
  }

  @Override
  public boolean existsByName(String networkName) {
    return this.dockerNetworkFacade.findByName(networkName) != null;
  }

  @Override
  public String findContainerWithNetworkAndIp(String networkName, String ip) {
    final var pair = this.dockerNetworkFacade.findContainerWithIp(networkName, ip);
    return mapOrNull(pair, Pair::getKey);
  }

  @Override
  public void disconnect(String networkId, String containerId) {
    this.dockerNetworkFacade.disconnect(networkId, containerId);
  }

  @Override
  public NetworkConnectionStatus connect(String networkNameOrId, String containerId) {
    return this.dockerNetworkFacade.connect(networkNameOrId, containerId);
  }

  @Override
  public void connect(String networkNameOrId, String containerId, String networkIp) {
    this.dockerNetworkFacade.connect(networkNameOrId, containerId, networkIp);
  }

  @Override
  public void connectRunningContainersToNetwork(String networkName, Predicate<ContainerCompact> p) {
    this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Containers.RUNNING_STATUS_LIST)
      .exec()
      .stream()
      .filter(it -> Boolean.FALSE.equals(isHostNetwork(it)))
      .filter(it -> !Containers.containsNetworkName(it, networkName))
      .filter(it -> p.test(ContainerCompactMapper.of(it)))
      .forEach(container -> this.connect(networkName, container.getId()))
    ;
  }

  static Boolean isHostNetwork(Container container) {
    final var config = container.getHostConfig();
    if (config == null) {
      return null;
    }
    final var networkMode = config.getNetworkMode();
    return Network.Name.HOST.equalTo(networkMode);
  }
}
