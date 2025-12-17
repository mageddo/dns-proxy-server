package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import com.mageddo.dnsproxyserver.config.Config;

public interface ConfigFileDAO {

  void save(Config config);

  Config find();

  void delete();
}
