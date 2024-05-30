package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;

import java.util.List;

public interface ContainerDAO {

  List<ContainerCompact> findNetworkContainers(String networkId);

  List<Container> findActiveContainersMatching(HostnameQuery query);

}
