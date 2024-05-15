package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;

public interface MultiSourceConfigDAO {
  Config find();
  int priority();
}
