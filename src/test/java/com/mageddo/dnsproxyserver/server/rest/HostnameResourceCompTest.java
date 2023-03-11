package com.mageddo.dnsproxyserver.server.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class HostnameResourceCompTest {

//  @BeforeAll
//  static void beforeAll(){
//    WebServer.start();
//  }
//
//  @AfterAll
//  static void afterAll(){
//    WebServer.stop();
//  }

  @Test
  void mustFindHostnamesButHasNoResult(){
    // arrange

    // act
    final var response = given()
      .queryParam("env", "")
      .get("/hostname/find")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo("[]"))
      .log()
    ;
  }

}
