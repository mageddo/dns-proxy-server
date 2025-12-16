package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;

public interface ConfigDAO {

  Config find();

  int priority();

}
