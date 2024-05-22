package com.mageddo.dnsproxyserver.config.dataprovider;

import java.util.Properties;

import static com.mageddo.commons.io.IoUtils.loadPropertiesFromResources;

public class ConfigPropDAO {

  private static final Properties resources = loadPropertiesFromResources("/application.properties");

  public static String getVersion(){
    return resources.getProperty("version", "unknown");
  }

}
