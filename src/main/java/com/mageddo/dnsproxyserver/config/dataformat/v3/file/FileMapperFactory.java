package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAOFileDelegate;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.YamlConfigDAO;

import com.mageddo.utils.Files;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FileMapperFactory {

  private final ConfigDAOFileDelegate configDAOFileDelegate;
  private final YamlConfigDAO yamlConfigDAO;

  public ConfigDAO findByExtension(String extension) {
    return switch (StringUtils.lowerCase(extension)) {
      case "json" -> this.configDAOFileDelegate;
      case "yaml" -> this.yamlConfigDAO;
      default -> throw new UnsupportedOperationException("Unsupported format: " + extension);
    };
  }

  public Config of(Path path) {
    final var extension = Files.findExtension(path);
    return null;
  }
}
