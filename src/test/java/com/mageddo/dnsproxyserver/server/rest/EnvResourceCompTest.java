package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs;
import dagger.sheath.junit.DaggerTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class EnvResourceCompTest {

  @BeforeEach
  void beforeEach(){
    Configs
      .getInstance()
      .resetConfigFile()
    ;
  }

  @Test
  void mustFindDefaultEnvAsActive() {
    // arrange

    // act
    final var response = given()
      .get("/env/active")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .contentType("application/json")
      .body(equalTo("""
        {"name":""}"""))
      .log()
    ;
  }

  @Test
  void mustListAvailableEnvs() {
    // arrange
    this.changeDefaultEnv();
//    this.mustCreateEnv();

    // act
    final var response = given()
      .get("/env")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo("""
        [{"name":""}]"""))
      .log()
    ;
  }

  @Test
  void changeDefaultEnv() {
    // arrange
    final var body = """
      {
        "name": "batata"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .put("/env/active")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.NO_CONTENT.getStatusCode())
      .body(equalTo(""))
      .log()
    ;

    final var activeEnv = JsonConfigs.loadConfigJson().getActiveEnv();
    assertEquals("batata", activeEnv);
  }

  @Test
  void mustCreateEnv() {
    // arrange
    final var body = """
      {
        "name": "batata"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .post("/env")
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
  void mustDeleteEnv() {
    // arrange
    this.mustCreateEnv();

    final var body = """
      {
        "name": "batata"
      }
      """;

    // act
    final var response = given()
      .contentType(ContentType.JSON)
      .body(body)
      .delete("/env")
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
}
