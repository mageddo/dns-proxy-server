package com.mageddo.dnsproxyserver.config.dataformat.v3.converter;

import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

public interface Converter {

  ConfigV3 parse();

  String serialize(ConfigV3 config);

  int priority();

}
