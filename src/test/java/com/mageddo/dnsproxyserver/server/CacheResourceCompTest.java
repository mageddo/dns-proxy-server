package com.mageddo.dnsproxyserver.server;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.di.Context;
import testing.Events;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@DaggerTest(component = Context.class, eventsHandler = Events.class)
class CacheResourceCompTest {

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
      .body(equalTo("{\"size\":0}"))
      .log()
    ;
  }
}
