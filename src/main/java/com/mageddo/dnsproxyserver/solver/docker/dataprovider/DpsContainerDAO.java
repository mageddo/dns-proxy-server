package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.solver.docker.Container;

public interface DpsContainerDAO {


  boolean isDpsContainer(String containerId);

  Container findDPSContainer();

  boolean isDpsRunningInsideContainer();

  void createDpsNetwork();
}
