package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.SolverSystem;
import testing.templates.HostnameTemplates;
import com.mageddo.dnsproxyserver.usecase.HostMachineService;
import com.mageddo.net.IP;
import dagger.sheath.InjectMock;
import dagger.sheath.junit.DaggerTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Rcode;
import testing.ContextSupplier;
import testing.Events;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class SolverSystemCompTest {

  @InjectMock
  HostMachineService machineService;

  @Inject
  SolverSystem solver;

  @Test
  void mustSolverHostMachineIp() {
    // arrange
    final var hostname = HostnameTemplates.HOST_DOCKER;
    final var query = Messages.aQuestion(hostname);

    doReturn(IP.of("192.168.0.1"))
      .when(this.machineService)
      .findHostMachineIP(any())
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    final var msg = res.getMessage();
    final var answer = Messages.findFirstAnswerRecordStr(msg);
    assertThat(answer, CoreMatchers.containsString(hostname));
    assertTrue(Messages.hasFlag(msg, Flags.RA));
    assertEquals("host.docker.\t\t30\tIN\tA\t192.168.0.1", answer);

    verify(this.machineService).findHostMachineIP(eq(IP.Version.IPV4));
  }

  @Test
  void mustSolveNoErrorWhenHostIsFoundButAddressIsNot() {

    // arrange
    final var query = Messages.quadAQuestion(HostnameTemplates.HOST_DOCKER);

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    final var msg = res.getMessage();
    assertEquals(Rcode.NOERROR, msg.getRcode());
    assertEquals("", Messages.detailedPrint(msg));

  }

}
