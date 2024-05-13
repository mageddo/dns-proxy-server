package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;

public interface DpsContainerDAO {


  boolean isDpsContainer(String containerId);

  Container findDPSContainer();

  boolean isDpsRunningInsideContainer();

  void createDpsNetwork();
}
