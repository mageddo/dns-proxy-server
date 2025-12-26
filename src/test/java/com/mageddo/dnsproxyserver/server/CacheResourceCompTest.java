package com.mageddo.dnsproxyserver.server;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.mageddo.dnsproxyserver.solver.cache.CacheName;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.cache.SolverCache;

import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.MessageTemplates;
import testing.templates.NamedResponseTemplates;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class CacheResourceCompTest {

  @Inject
  @CacheName(name = Name.GLOBAL)
  SolverCache cache;

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
    this.cache.handleToMsg(MessageTemplates.acmeAQuery(), NamedResponseTemplates::of);

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
