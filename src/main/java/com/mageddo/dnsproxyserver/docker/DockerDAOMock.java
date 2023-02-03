package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.AllArgsConstructor;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
@Alternative
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAOMock implements DockerDAO {
  @Override
  public String findHostIp(Hostname host) {
    if (Objects.equals("acme.com", host)) {
      return "192.168.0.1";
    }
    return null;
  }

  @Override
  public String findHostMachineIp() {
    return "127.0.0.1";
  }
}
