package com.mageddo.http;

import com.mageddo.http.codec.Encoders;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

class WebServerCompTest {
  @Test
  void mustSolveConfiguredPath(){
    // arrange
    final var body = "Hello World!";
    final var theServer = new WebServer(server -> {
      server.get("/hello-world", exchange -> Encoders.encodePlain(exchange, body));
    });
    theServer.start(8185);

    // act
    final var response = given()
      .port(8185)
      .get("/hello-world")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(200)
      .body(equalTo(body))
    ;
  }
}
