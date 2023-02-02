package com.mageddo.dnsproxyserver.server.dns.solver;

import com.github.dockerjava.api.model.Network;
import com.mageddo.json.JsonUtils;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class SolverSystemIT {

  @Test
  void mustDeserializeNetworkVO(){
    // arrange
    final var json = """
      {
        "Name": "bridge"
      }
      """;

    // act
    final var vo = JsonUtils.readValue(json, Network.class);

    // assert
    assertEquals("bridge", vo.getName());
  }

}
