package com.mageddo.dnsproxyserver.config.dataformat.v3.converter;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConverterFactory {

  private final ConverterJson jsonConverter;
  private final ConverterYaml yamlConverter;

  public Converter find(String format) {
    return switch (StringUtils.lowerCase(format)) {
      case "json" -> this.jsonConverter;
      case "yml", "yaml" -> this.yamlConverter;
      default -> throw new UnsupportedOperationException("Unsupported format: " + format);
    };
  }
}
