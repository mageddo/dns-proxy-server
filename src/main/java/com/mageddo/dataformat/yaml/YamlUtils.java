package com.mageddo.dataformat.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.UncheckedIOException;

public class YamlUtils {

  public static final YAMLMapper mapper = YAMLMapper
    .builder()
    .enable(SerializationFeature.INDENT_OUTPUT)
    .build();

  public static String format(String yaml) {
    try {
      return mapper.writeValueAsString(mapper.readTree(yaml));
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String writeValueAsString(Object o) {
    try {
      return mapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(String yaml, Class<T> clazz) {
    try {
      return mapper.readValue(yaml, clazz);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
