package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@QuarkusTest
class SolverSystemTest {

  @InjectMock(convertScopes = true)
  DockerDAO dockerDAO;

  @Inject
  SolverSystem solver;

  @Test
  void mustSolverHostMachineIp(){
    // arrange
    final var hostname = "host.docker.";
    final var query = MessageTemplates.buildAQuestionFor(hostname);

    doReturn( "192.168.0.1")
      .when(this.dockerDAO)
      .findHostMachineIp()
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    final var answer = Messages.findFirstAnswerRecord(res);
    assertThat(answer, CoreMatchers.containsString(hostname));
    assertEquals("host.docker.\t\t30\tIN\tA\t192.168.0.1", answer);

    verify(this.dockerDAO).findHostMachineIp();
  }

}
