package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.mageddo.net.IP;

public interface DockerDAO {

  default IP findHostMachineIp() {
    return this.findHostMachineIp(IP.Version.IPV4);
  }

  boolean isConnected();

  IP findHostMachineIp(IP.Version version);

}
