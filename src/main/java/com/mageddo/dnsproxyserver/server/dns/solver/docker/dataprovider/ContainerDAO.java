package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.ContainerCompact;

import java.util.List;

public interface ContainerDAO {

  List<ContainerCompact> findNetworkContainers(String networkId);

  List<Container> findActiveContainersMatching(HostnameQuery query);

}
