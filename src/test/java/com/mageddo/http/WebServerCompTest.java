package com.mageddo.http;

import com.mageddo.http.codec.Encoders;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

class WebServerCompTest {

  public static final int PORT = 8185;

  @Test
  void mustHandleConfiguredMapper() throws Exception {
    // arrange
    final var body = "Hello World!";
    final var theServer = new WebServer(server -> {
      server.get("/hello-world", exchange -> Encoders.encodePlain(exchange, body));
    });
    try (theServer) {

      theServer.start(PORT);

      // act
      final var response = given()
        .port(PORT)
        .get("/hello-world")
        .then()
        .log()
        .ifValidationFails();

      // assert
      response
        .statusCode(HttpStatus.OK)
        .body(equalTo(body))
      ;
    }
  }

  @Test
  void mustFallbackToNotFoundPage() throws Exception {
    // arrange
    final var theServer = new WebServer(server -> {});
    try (theServer) {

      theServer.start(PORT);

      // act
      final var response = given()
        .port(PORT)
        .get("/hello-world")
        .then()
        .log()
        .ifValidationFails();

      // assert
      response
        .statusCode(HttpStatus.NOT_FOUND)
        .body(equalTo(WebServer.DEFAULT_RES_BODY))
      ;
    }
  }
}
