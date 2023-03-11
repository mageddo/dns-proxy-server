package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.docker.DockerNetworkService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
class NetworkResourceCompTest {

  @InjectMock(convertScopes = true)
  DockerNetworkService networkService;

  @Test
  void changeDefaultEnv() {
    // arrange
    doReturn(List.of("6c99cf8211c282a50fe5be34dff1aa0c8ce9b0aa194dcf6b80339898de07be59"))
      .when(this.networkService)
      .disconnectContainers(anyString())
    ;

    // act
    final var response = given()
      .queryParam("networkId", "net1")
      .delete("/network/disconnect-containers")
      .then()
      .log()
      .ifValidationFails();

    // assert
    response
      .statusCode(Response.Status.OK.getStatusCode())
      .body(equalTo("[\"6c99cf8211c282a50fe5be34dff1aa0c8ce9b0aa194dcf6b80339898de07be59\"]"))
      .log()
    ;

  }
}
