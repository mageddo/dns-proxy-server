package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

public interface Parser {

  ConfigV3 parse();

  String marshal(ConfigV3 config);

  int priority();

}
