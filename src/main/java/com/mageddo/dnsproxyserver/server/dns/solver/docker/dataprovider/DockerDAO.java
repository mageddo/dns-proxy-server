package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.mageddo.net.IP;

public interface DockerDAO {
  IP findHostMachineIp(IP.Version version);
}
