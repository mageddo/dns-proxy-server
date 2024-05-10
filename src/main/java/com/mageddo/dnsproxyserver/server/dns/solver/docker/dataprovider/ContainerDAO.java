package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;

import java.util.List;

public interface ContainerDAO {

  boolean isDpsContainer(String containerId);

  Container findDPSContainer();

  List<Container> findActiveContainersInspectMatching(HostnameQuery query);
}
