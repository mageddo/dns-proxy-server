package com.mageddo.dnsproxyserver.solver.system;

import com.mageddo.dnsproxyserver.solver.Responses;
import com.mageddo.net.IP;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.MessageTemplates;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SolverSystemTest {

  @Spy
  @InjectMocks
  SolverSystem solver;

  @Test
  void mustReturnNullWhenTypeIsNotSupported() {
    // arrange
    final var query = MessageTemplates.acmeSoaQuery();

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNull(res);
  }


  @Test
  void mustBeAuthoritative() {

    doReturn(IP.of("192.168.0.1"))
        .when(this.solver)
        .findHostMachineIP(IP.Version.IPV4);

    final var query = MessageTemplates.hostDockerAQuery();

    final var res = this.solver.handle(query);

    assertNotNull(res);
    assertTrue(Responses.isAuthoritative(res));
  }

  @Test
  void mustAnswerNoErrorWhenHasNotToAnswerButHostNameMatches() {

    doReturn(null)
        .when(this.solver)
        .findHostMachineIP(any());


    final var query = MessageTemplates.hostDockerAQuery();

    final var res = this.solver.handle(query);

    assertNotNull(res);
    assertTrue(Responses.isSuccess(res));
  }

  @Test
  void mustAnswerNoErrorWhenQueryAAAAndHasNotToAnswerButHostNameMatches() {

    doReturn(null)
        .when(this.solver)
        .findHostMachineIP(any());


    final var query = MessageTemplates.hostDockerQuadAQuery();

    final var res = this.solver.handle(query);

    assertNotNull(res);
    assertTrue(Responses.isSuccess(res));
  }

  @Test
  void musReturnNullWhenHostNameDoesntMatch() {

    final var query = MessageTemplates.randomHostnameAQuery();

    final var res = this.solver.handle(query);

    assertNull(res);
  }
}
