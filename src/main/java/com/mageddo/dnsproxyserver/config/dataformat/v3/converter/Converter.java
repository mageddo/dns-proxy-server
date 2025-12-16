package com.mageddo.dnsproxyserver.config.dataformat.v3.converter;

import com.mageddo.dnsproxyserver.config.Config;

public interface Converter {
  Config of(String raw);

  String to(Config config);
}
