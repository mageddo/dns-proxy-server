package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.docker.NetworkConnectionStatus;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerCompactMapper;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.NetworkMapper;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.commons.lang.Objects.mapOrNull;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class NetworkDAODefault implements NetworkDAO {

  private final DockerClient dockerClient;
  private final DockerNetworkDAO dockerNetworkDAO;

  @Override
  public Network findById(String networkId) {
    return NetworkMapper.of(this.dockerNetworkDAO.findById(networkId));
  }

  @Override
  public Network findByName(String networkName) {
    return NetworkMapper.of(this.dockerNetworkDAO.findByName(networkName));
  }

  @Override
  public boolean existsByName(String networkName) {
    return this.dockerNetworkDAO.findByName(networkName) != null;
  }

  @Override
  public String findContainerWithNetworkAndIp(String networkName, String ip) {
    final var pair = this.dockerNetworkDAO.findContainerWithIp(networkName, ip);
    return mapOrNull(pair, Pair::getKey);
  }

  @Override
  public void disconnect(String networkId, String containerId) {
    this.dockerNetworkDAO.disconnect(networkId, containerId);
  }

  @Override
  public NetworkConnectionStatus connect(String networkNameOrId, String containerId) {
    return this.dockerNetworkDAO.connect(networkNameOrId, containerId);
  }

  @Override
  public void connect(String networkNameOrId, String containerId, String networkIp) {
    this.dockerNetworkDAO.connect(networkNameOrId, containerId, networkIp);
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
