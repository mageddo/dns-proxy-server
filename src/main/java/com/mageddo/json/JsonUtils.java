package com.mageddo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;

public final class JsonUtils {

  public static final String PROVIDER_PATH = "/META-INF/services/com.mageddo.common.jackson.Providers";

  private static ObjectMapper instance;
  private static ObjectMapper prettyInstance;
  private static ObjectMapper noAutoCloseableInstance;

  static {
    configureInstance();
  }

  private JsonUtils() {
  }

  public static ObjectMapper instance() {
    return instance;
  }

  public static ObjectMapper prettyInstance() {
    return prettyInstance;
  }

  public static ObjectMapper noAutoCloseableInstance() {
    return noAutoCloseableInstance;
  }

  public static ObjectMapper noAutoCloseable(ObjectMapper objectMapper) {
    return objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
  }

  public static ObjectMapper prettyInstance(ObjectMapper objectMapper) {
    return objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static ObjectMapper objectMapper() {
    final SimpleModule m = new SimpleModule();
    return new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .registerModule(new JavaTimeModule())
      .registerModule(m);
  }

  public static ObjectMapper setInstance(ObjectMapper objectMapper) {
    instance = objectMapper;
    prettyInstance = prettyInstance(instance.copy());
    noAutoCloseableInstance = noAutoCloseable(instance.copy());
    return instance;
  }

  private static ObjectMapper setup(boolean production) {
    if (production) {
      instance.disable(SerializationFeature.INDENT_OUTPUT);
    }
    return instance;
  }

  public static JsonNode readTree(InputStream in) {
    try {
      return instance.readTree(in);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static JsonNode readTree(String o) {
    try {
      return instance.readTree(o);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String writeValueAsString(Object o) {
    try {
      return instance.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(String value, Class<T> clazz) {
    try {
      return instance.readValue(value, clazz);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(String value, TypeReference<T> t) {
    try {
      return instance.readValue(value, t);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(JsonParser jsonParser, TypeReference<T> t) {
    try {
      return instance.readValue(jsonParser, t);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String prettify(String json) {
    try {
      return prettyInstance.writeValueAsString(instance.readTree(json));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(InputStream in, Class<T> o) {
    try {
      return instance.readValue(in, o);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T readValue(JsonParser data, Class<T> clazz) {
    try {
      return instance.readValue(data, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void configureInstance() {
    try {
      final String line = readFirstLine();
      if (line == null) {
        setInstance(objectMapper());
      } else {
        final Class<JsonConfig> clazz = (Class<JsonConfig>) Class.forName(line);
        setInstance(clazz.getDeclaredConstructor().newInstance().objectMapper());
      }
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException |
             InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  static String readFirstLine() {
    final InputStream in = JsonUtils.class.getResourceAsStream(PROVIDER_PATH);
    if (in == null) {
      return null;
    }
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
      return br.readLine();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String prettyWriteValueAsString(Object v) {
    try {
      return prettyInstance().writeValueAsString(v);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

}
