package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.templates.EntryTemplates;
import com.mageddo.dnsproxyserver.templates.docker.SolverTemplates;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static com.mageddo.dnsproxyserver.server.dns.Hostnames.toAbsoluteName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@QuarkusTest
class SolverLocalDBTest {

  @Inject
  SolverLocalDB solver;

  @Inject
  ConfigDAO configDAO;

  @InjectMock(convertScopes = true)
  SolverProvider solverProvider;

  @Test
  void mustNotSolveFromLocalDBWhenNoJsonIsConfigured() {

    // arrange
    final var msg = Messages.aQuestion("acme.com.");

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNull(res);
    verify(this.solverProvider, never()).getSolversExcludingLocalDB();

  }

  @Test
  void mustSolveFromLocalDB() {

    // arrange
    final var host = "acme.com";
    final var entry = EntryTemplates.a(host);
    this.configDAO.addEntry(Config.Env.DEFAULT_ENV, entry);

    final var msg = Messages.aQuestion(toAbsoluteName(host));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
        "acme.com.    45  IN  A  10.10.0.1",
        Messages.detailedPrint(res)
    );

  }

  @Test
  void mustSolveCnameFromLocalDB() {

    // arrange
    final var from = "www.acme.com";
    final var to = "acme.com";
    final var entry = EntryTemplates.cname(from, to);
    this.configDAO.addEntry(Config.Env.DEFAULT_ENV, entry);

    doReturn(SolverTemplates.mockTo192())
        .when(this.solverProvider)
        .getSolversExcludingLocalDB()
    ;

    final var msg = Messages.aQuestion(toAbsoluteName(from));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
        "www.acme.com.    45  IN  CNAME  acme.com. | acme.com.    30  IN  A  192.168.1.8",
        Messages.detailedPrint(res)
    );

  }


  @Test
  void mustSolveAAARecordAsAFromLocalDB() {

    // arrange
    this.configDAO.addEntry(Config.Env.DEFAULT_ENV, EntryTemplates.acmeAAAA());
    final var msg = Messages.quadAQuestion(toAbsoluteName(EntryTemplates.ACME_COM));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
      "acme.com.    45  IN  A  10.10.0.1",
      Messages.detailedPrint(res)
    );

  }


}
