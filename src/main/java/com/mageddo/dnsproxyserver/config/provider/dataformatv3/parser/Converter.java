package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

public interface Converter {

  ConfigV3 parse();

  String serialize(ConfigV3 config);

  int priority();

}
