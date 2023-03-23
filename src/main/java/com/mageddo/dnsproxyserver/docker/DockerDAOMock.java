package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.mageddo.net.IP;
import lombok.AllArgsConstructor;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Alternative
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAOMock implements DockerDAO {

  @Override
  public IP findHostMachineIp() {
    return IP.of("127.0.0.1");
  }

  @Override
  public boolean isConnected() {
    return true;
  }

  @Override
  public List<Container> findActiveContainers() {
    throw new UnsupportedOperationException();
  }

  @Override
  public InspectContainerResponse inspect(String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String findHostMachineIpRaw() {
    throw new UnsupportedOperationException();
  }

}
