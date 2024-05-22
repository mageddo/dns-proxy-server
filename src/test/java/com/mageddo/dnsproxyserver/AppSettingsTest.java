package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.application.AppSettings;
import testing.templates.ConfigTemplates;
import com.mageddo.logback.LogbackUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppSettingsTest {
  @Test
  void mustLogLevelInSl4jConvetion(){
    // arrange
    final var config = ConfigTemplates.defaultWithoutId();

    // act
    new AppSettings().setupLogs(config);

    // assert
    assertEquals("WARN", LogbackUtils.getLogLevel("com.mageddo").levelStr);
  }


}
