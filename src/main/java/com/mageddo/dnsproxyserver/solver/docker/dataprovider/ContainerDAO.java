package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import java.util.List;

import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;

public interface ContainerDAO {

  List<ContainerCompact> findNetworkContainers(String networkId);

  List<Container> findActiveContainersMatching(HostnameQuery query);

}
