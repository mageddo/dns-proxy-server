package com.mageddo.dnsproxyserver.config.dataformat.v2;

import com.mageddo.dnsproxyserver.config.Config;

public interface ConfigDAO {

  Config find();

  int priority();

}
