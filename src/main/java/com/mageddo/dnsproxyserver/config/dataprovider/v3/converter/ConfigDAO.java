package com.mageddo.dnsproxyserver.config.dataprovider.v3.converter;

import com.mageddo.dnsproxyserver.config.dataprovider.v3.ConfigV3;

public interface ConfigDAO {

  ConfigV3 find();

  String serialize(ConfigV3 config);

  int priority();

}
