package com.mageddo.dnsproxyserver.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;

public interface LogSettingsDAO {
  void setupLogFile(Config config);

  void setupLogLevel(Config config);
}
