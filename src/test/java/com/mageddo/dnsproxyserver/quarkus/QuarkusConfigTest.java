package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.dnsproxyserver.templates.ConfigTemplates;
import org.junit.jupiter.api.Test;

import static com.mageddo.dnsproxyserver.quarkus.QuarkusConfig.DPS_LOG_LEVEL_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuarkusConfigTest {
  @Test
  void mustLogLevelInSl4jConvetion(){
    // arrange
    final var config = ConfigTemplates.withoutId();

    // act
    QuarkusConfig.setup(config);

    // assert
    assertEquals(System.getProperty(DPS_LOG_LEVEL_KEY), "WARN");
  }


}
