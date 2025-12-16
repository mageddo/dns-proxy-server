package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

public interface ConfigDAO {

  ConfigV3 find();

  int priority();

}
