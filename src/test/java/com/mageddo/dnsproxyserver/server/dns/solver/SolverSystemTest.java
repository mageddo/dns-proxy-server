package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

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

}
