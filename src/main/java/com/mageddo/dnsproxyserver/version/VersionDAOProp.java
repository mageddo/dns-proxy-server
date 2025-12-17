package com.mageddo.dnsproxyserver.version;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.NoArgsConstructor;

import static com.mageddo.commons.io.IoUtils.loadPropertiesFromResources;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class VersionDAOProp implements VersionDAO {

  private static final Properties resources = loadPropertiesFromResources(
      "/application.properties");

  public String findVersion() {
    return resources.getProperty("version", "unknown");
  }

}
