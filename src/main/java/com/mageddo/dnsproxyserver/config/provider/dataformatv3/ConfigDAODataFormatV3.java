package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;

public class ConfigDAODataFormatV3 implements ConfigDAO {
  @Override
  public Config find() {
    return null;
  }

  @Override
  public int priority() {
    return 0;
  }
}
