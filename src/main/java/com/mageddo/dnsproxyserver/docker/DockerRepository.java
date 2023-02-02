package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.server.dns.Hostname;

public interface DockerRepository {
  /**
   *
   * @param host
   * @return the host ip
   */
  String findHostIp(Hostname host);

  String findHostMachineIp();
}
