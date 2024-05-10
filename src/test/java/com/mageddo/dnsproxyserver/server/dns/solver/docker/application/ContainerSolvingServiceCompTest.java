package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import dagger.sheath.InjectMock;
import dagger.sheath.InjectSpy;
import dagger.sheath.junit.DaggerTest;

// todo #444
@DaggerTest(component = Context.class)
class ContainerSolvingServiceCompTest {

  @InjectMock
  DockerFacade dockerFacade;

  @InjectMock
  DockerDAO dockerDAO;

  @InjectMock
  DockerNetworkFacade dockerNetworkDAO;

  @InjectMock
  MatchingContainerService matchingContainerService;

  @InjectSpy
  ContainerSolvingService containerSolvingService;



}
