package com.mageddo.dnsproxyserver.solver;

import javax.inject.Inject;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFileDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.MutableConfigDAO;
import com.mageddo.dnsproxyserver.di.Context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dagger.sheath.InjectMock;
import dagger.sheath.junit.DaggerTest;
import testing.templates.EntryTemplates;
import testing.templates.docker.SolverTemplates;

import static com.mageddo.dns.utils.Hostnames.toAbsoluteName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DaggerTest(component = Context.class)
class SolverLocalDBCompTest {

  @Inject
  SolverLocalDB solver;

  @Inject
  MutableConfigDAO mutableConfigDAO;

  @Inject
  ConfigFileDAO configFileDAO;

  @InjectMock
  SolverProvider solverProvider;

  @AfterEach
  @BeforeEach
  void each() {
    this.configFileDAO.delete();
  }

  @Test
  void mustNotSolveFromLocalDBWhenNoJsonIsConfigured() {

    // arrange
    final var msg = Messages.aQuestion("acme.com.");

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNull(res);
    verify(this.solverProvider, never()).getSolversExcluding(SolverLocalDB.class);

  }

  @Test
  void mustSolveFromLocalDB() {

    // arrange
    final var host = "acme.com";
    final var entry = EntryTemplates.a(host);
    this.mutableConfigDAO.addEntry(Config.Env.DEFAULT_ENV, entry);

    final var msg = Messages.aQuestion(toAbsoluteName(host));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
        "acme.com.    45  IN  A  10.10.0.1",
        Messages.detailedPrint(res.getMessage())
    );

  }

  @Test
  void mustSolveCnameFromLocalDB() {

    // arrange
    final var from = "www.acme.com";
    final var to = "acme.com";
    final var entry = EntryTemplates.cname(from, to);
    this.mutableConfigDAO.addEntry(Config.Env.DEFAULT_ENV, entry);

    doReturn(SolverTemplates.mockTo192())
        .when(this.solverProvider)
        .getSolversExcluding(SolverLocalDB.class)
    ;

    final var msg = Messages.aQuestion(toAbsoluteName(from));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
        "www.acme.com.    45  IN  CNAME  acme.com. | acme.com.    30  IN  A  192.168.1.8",
        Messages.detailedPrint(res.getMessage())
    );

  }

  @Test
  void mustSolveAAARecordAsAFromLocalDB() {

    // arrange
    this.mutableConfigDAO.addEntry(Config.Env.DEFAULT_ENV, EntryTemplates.acmeQuadA());
    final var msg = Messages.quadAQuestion(toAbsoluteName(EntryTemplates.ACME_COM));

    // act
    final var res = this.solver.handle(msg);

    // assert
    assertNotNull(res);
    assertEquals(
        "acme.com.    45  IN  AAAA  2001:db8:1:0:0:0:0:2",
        Messages.detailedPrint(res.getMessage())
    );

  }


}
