package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;

public interface ContainerDAO {
  Container findDPSContainer();
}
