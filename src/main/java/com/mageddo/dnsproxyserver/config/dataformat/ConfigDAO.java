package com.mageddo.dnsproxyserver.config.dataformat;

import com.mageddo.dnsproxyserver.config.Config;

public interface ConfigDAO {

  Config find();

  int priority();

}
