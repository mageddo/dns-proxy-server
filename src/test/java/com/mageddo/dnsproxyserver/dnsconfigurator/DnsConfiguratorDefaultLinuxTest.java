package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.sun.jna.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(MockitoExtension.class)
class DnsConfiguratorDefaultLinuxTest {

  @InjectMocks
  DnsConfiguratorDefault configurator;

  @BeforeEach
  void beforeEach(){
    assumeTrue(Platform.isLinux());
  }

  @Test
  void mustFailAsLinuxIsNotSupported(){
    // arrange

    // act
    assertThrows(UnsupportedOperationException.class, () -> {
      this.configurator.restore();
    });

    // assert

  }
}
