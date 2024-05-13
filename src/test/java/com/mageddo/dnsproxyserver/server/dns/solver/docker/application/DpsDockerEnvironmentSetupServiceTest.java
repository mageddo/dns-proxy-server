package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DpsDockerEnvironmentSetupServiceTest {

  @Spy
  @InjectMocks
  DpsDockerEnvironmentSetupService dpsDockerEnvironmentSetupService;

}
