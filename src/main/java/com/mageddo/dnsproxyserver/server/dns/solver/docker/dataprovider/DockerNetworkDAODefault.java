package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
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
public class DockerNetworkDAODefault implements DockerNetworkDAO {

  private final DockerNetworkFacade dockerNetworkFacade;

  @Override
  public Network findById(String networkId) {
    return NetworkMapper.of(this.dockerNetworkFacade.findById(networkId));
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
  public void connect(String networkNameOrId, String containerId) {
    this.dockerNetworkFacade.connect(networkNameOrId, containerId);
  }

  @Override
  public void connect(String networkNameOrId, String containerId, String networkIp) {
    this.dockerNetworkFacade.connect(networkNameOrId, containerId, networkIp);
  }

  @Override
  public void connectRunningContainers(String networkName, Predicate<Container> p) {
    this.dockerNetworkFacade.connectRunningContainers(networkName, p);
  }
}
