package com.mageddo.dnsproxyserver.server;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class CacheResourceCompTest {

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
