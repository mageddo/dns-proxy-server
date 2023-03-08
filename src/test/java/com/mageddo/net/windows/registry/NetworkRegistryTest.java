package com.mageddo.net.windows.registry;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkRegistryTest {

  @BeforeEach
  void beforeEach() {
    assertTrue(Platform.isWindows());
  }

  @Test
  void mustInterfacesWithIp() {
    // arrange

    // act
    final var ids = NetworkRegistry.findNetworksWithIpIds();

    // assert
    assertFalse(ids.isEmpty());
    final var first = ids.stream().findFirst().get();
    assertTrue(first.startsWith("{") && first.endsWith("}"), first);
  }


  @Test
  void mustFindInterfaceChangeDnsThenRestoreIt() {
    // arrange
    final var nid = NetworkRegistry.findNetworksWithIpIds()
      .stream()
      .findFirst()
      .get();
    final var expectedConfiguredDns = Collections.singletonList("8.8.8.8");

    // act
    final var ex = assertThrows(Win32Exception.class, () -> NetworkRegistry.updateDnsServer(nid, expectedConfiguredDns));

    // arrange
    assertEquals(WinError.ERROR_ACCESS_DENIED, ex.getErrorCode());
  }
}
