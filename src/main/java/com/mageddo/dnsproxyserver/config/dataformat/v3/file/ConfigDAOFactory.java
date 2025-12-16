package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;

import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.YamlConfigDAO;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOFactory {

  private final JsonConfigDAO jsonConfigDAO;
  private final YamlConfigDAO yamlConfigDAO;

  public ConfigDAO findByExtension(String extension) {
    return switch (StringUtils.lowerCase(extension)) {
      case "json" -> this.jsonConfigDAO;
      case "yaml" -> this.yamlConfigDAO;
      default -> throw new UnsupportedOperationException("Unsupported format: " + extension);
    };
  }
}
