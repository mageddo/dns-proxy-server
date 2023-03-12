package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import dagger.Events;
import dagger.sheath.junit.DaggerTest;
import dagger.sheath.junit.InjectMock;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DaggerTest(component = Context.class, eventsHandler = Events.class)
class SolverSystemTest {

  @InjectMock
  DockerDAO dockerDAO;

  @Inject
  SolverSystem solver;

  @Test
  void mustSolverHostMachineIp(){
    // arrange
    final var hostname = "host.docker.";
    final var query = MessageTemplates.buildAQuestionFor(hostname);

    doReturn(IP.of("192.168.0.1"))
      .when(this.dockerDAO)
      .findHostMachineIp()
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    final var answer = Messages.findFirstAnswerRecordStr(res);
    assertThat(answer, CoreMatchers.containsString(hostname));
    assertEquals("host.docker.\t\t30\tIN\tA\t192.168.0.1", answer);

    verify(this.dockerDAO).findHostMachineIp();
  }

}
