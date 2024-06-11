package com.mageddo.dnsproxyserver.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.dataprovider.LogSettingsDAO;
import com.mageddo.dnsproxyserver.dataprovider.LogSettingsDAOSlf4j;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class LogSettings {

  /**
   * Injetando na mão, pois precisa ser feito antes de iniciar o contexto.
    */
  private final LogSettingsDAO logSettingsDAO = new LogSettingsDAOSlf4j();

  public void setupLogs(Config config) {
    this.logSettingsDAO.setupLogFile(config);
    this.logSettingsDAO.setupLogLevel(config);
  }

}
