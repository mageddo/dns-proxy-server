package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.application.Configs;
import dagger.sheath.junit.DaggerTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class HostnameResourceCompTest {

  @BeforeEach
  void beforeEach(){
    Configs
      .getInstance()
      .resetConfigFile()
    ;
  }

  @Test
  void mustFindHostnamesAndFindDefaultResult(){
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
      .body(equalTo("""
        [{"id":"1","hostname":"dps-sample.dev","ip":"192.168.0.254","target":null,"ttl":30,"type":"A","env":null}]"""))
      .log()
    ;
  }

  @Test
  void mustFindEnvHostnames(){
    // arrange
    this.mustCreateHostname();

    // act
    final var response = given()
      .queryParam("env", "batata-env")
      .get("/hostname/find")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo("""
         [{"id":"1231","hostname":"acme.com","ip":"192.168.0.1","target":null,"ttl":31,"type":"A","env":null}]"""))
      .log()
    ;
  }

  @Test
  void mustCreateHostname(){

    // arrange
    final var body = """
      {
        "id": 1231,
        "hostname": "acme.com",
        "ip": "192.168.0.1",
        "ttl": 31,
        "type": "A",
        "env": "batata-env"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .post("/hostname")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.NO_CONTENT.getStatusCode())
      .body(equalTo(""))
      .log()
    ;

  }

  @Test
  void mustUpdateHostname(){

    // arrange
    this.mustCreateHostname();

    final var body = """
      {
        "id": 1231,
        "hostname": "acme.com",
        "ip": [192, 168, 0, 5],
        "ttl": 31,
        "type": "A",
        "env": "batata-env"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .put("/hostname")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.NO_CONTENT.getStatusCode())
      .body(equalTo(""))
      .log()
    ;

  }

  @Test
  void mustDeleteHostname(){

    // arrange
    this.mustCreateHostname();

    final var body = """
      {
        "env": "batata-env",
        "hostname": "acme.com"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .delete("/hostname")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo(""))
      .log()
    ;

  }

}
