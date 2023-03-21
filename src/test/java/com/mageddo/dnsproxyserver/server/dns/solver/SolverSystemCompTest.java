package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.usecase.HostMachineService;
import dagger.sheath.InjectMock;
import dagger.sheath.junit.DaggerTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class SolverSystemCompTest {

  @InjectMock
  HostMachineService machineService;

  @Inject
  SolverSystem solver;

  @Test
  void mustSolverHostMachineIp(){
    // arrange
    final var hostname = "host.docker.";
    final var query = Messages.aQuestion(hostname);

    doReturn(IP.of("192.168.0.1"))
      .when(this.machineService)
      .findHostMachineIP()
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    final var answer = Messages.findFirstAnswerRecordStr(res.getMessage());
    assertThat(answer, CoreMatchers.containsString(hostname));
    assertEquals("host.docker.\t\t30\tIN\tA\t192.168.0.1", answer);

    verify(this.machineService).findHostMachineIP();
  }

}
