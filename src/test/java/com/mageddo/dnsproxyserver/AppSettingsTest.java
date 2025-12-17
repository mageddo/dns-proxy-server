package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.LogSettings;
import com.mageddo.logback.LogbackUtils;

import org.junit.jupiter.api.Test;

import testing.templates.ConfigTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppSettingsTest {
  @Test
  void mustLogLevelInSl4jConversion() {
    // arrange
    final var config = ConfigTemplates.defaultWithoutId();

    // act
    new LogSettings().setupLogs(config);

    // assert
    assertEquals("WARN", LogbackUtils.getLogLevel("com.mageddo").levelStr);
  }


}
