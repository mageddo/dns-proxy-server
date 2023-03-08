package com.mageddo.net.windows.registry;

import com.sun.jna.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkRegistryTest {

  @BeforeEach
  void beforeEach(){
    assertTrue(Platform.isWindows());
  }

  @Test
  void mustInterfacesWithIp(){
    // arrange

    // act
    final var ids = NetworkRegistry.findNetworksWithIpIds();

    // assert
    assertTrue(!ids.isEmpty());
    final var first = ids.stream().findFirst().get();
    assertTrue(first.startsWith("{") && first.endsWith("}"), first);
  }
}
