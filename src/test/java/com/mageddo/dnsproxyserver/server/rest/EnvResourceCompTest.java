package com.mageddo.dnsproxyserver.server.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFileDAO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;
import io.restassured.http.ContentType;
import testing.ContextSupplier;
import testing.Events;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class EnvResourceCompTest {

  @Inject
  ConfigFileDAO configFileDAO;

  @AfterEach
  @BeforeEach
  void each() {
    this.configFileDAO.delete();
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
