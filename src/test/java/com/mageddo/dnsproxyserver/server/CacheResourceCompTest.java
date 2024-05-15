package com.mageddo.dnsproxyserver.server;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverCache;
import testing.templates.MessageTemplates;
import testing.templates.ResponseTemplates;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class CacheResourceCompTest {

  @Inject
  @CacheName(name = Name.GLOBAL)
  SolverCache cache;

  @BeforeEach
  void beforeEach(){
    Configs
      .getInstance()
      .resetConfigFile()
    ;
  }

  @Test
  void mustFindCacheSize() {
    // arrange

    // act
    final var response = given()
      .get("/v1/caches/size")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo("""
        {"GLOBAL":0,"REMOTE":0}"""))
      .log()
    ;
  }

  @Test
  void mustFindCaches() {
    // arrange
    this.cache.handle(MessageTemplates.acmeAQuery(), ResponseTemplates::to);

    // act
    final var response = given()
      .get("/v1/caches")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body("GLOBAL", notNullValue())
      .body("REMOTE.'A-acme.com'", notNullValue())
      .body("REMOTE.'A-acme.com'.ttl", equalTo("PT5S"))
      .log()
    ;
  }

  @Test
  void mustFilterCaches() {
    // arrange

    // act
    final var response = given()
      .param("name", "GLOBAL")
      .get("/v1/caches")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body("GLOBAL", notNullValue())
      .body("REMOTE", nullValue())
      .log()
    ;
  }
}
