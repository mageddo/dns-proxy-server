package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Predicate;

@Singleton
public class DockerNetworkDAOMock implements DockerNetworkDAO {
  @Override
  public Network findById(String id) {
    return null;
  }

  @Override
  public Network findByName(String networkName) {
    return null;
  }

  @Override
  public Pair<String, ContainerNetwork> findContainerWithIp(String networkName, String ip) {
    return null;
  }

  @Override
  public List<Container> findNetworkContainers(String networkId) {
    return null;
  }

  @Override
  public void disconnect(String networkId, String containerId) {

  }

  @Override
  public void connect(String networkNameOrId, String containerId) {

  }

  @Override
  public void connect(String networkNameOrId, String containerId, String ip) {

  }

  @Override
  public void connectRunningContainers(String networkName) {

  }

  @Override
  public void connectRunningContainers(String networkName, Predicate<Container> p) {

  }

  @Override
  public boolean exists(String networkId) {
    return false;
  }

  @Override
  public boolean existsByName(String networkName) {
    return false;
  }
}
