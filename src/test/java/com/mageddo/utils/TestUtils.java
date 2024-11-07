package com.mageddo.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@UtilityClass
public class TestUtils {

  public static final ObjectMapper objectMapper = JsonMapper.builder()
    .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
    .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
    .enable(SerializationFeature.INDENT_OUTPUT)
    .build();

  @SneakyThrows
  public static String readString(String path) {
    final InputStream resource = TestUtils.class.getResourceAsStream(path);
    assertNotNull(resource, "file not found: " + path);
    return IOUtils.toString(resource, "UTF-8");
  }

  public static String readAndSortJson(Path path) {
    return sortJson(readString(path));
  }

  @SneakyThrows
  public static String readAndSortJson(String path) {
    return sortJson(readString(path));
  }

  public static String readAndSortJsonExcluding(Object o, String... excludingFields) {
    return sortJsonExcluding(JsonUtils.writeValueAsString(o), excludingFields);
  }

  @SneakyThrows
  public static String readSortDonWriteNullsAndExcludeFields(Object o, String... excludingFields) {
    final var om = dontWriteNonNullObjectMapper();
    final var excludedFields = om.readTree(sortJsonExcluding(om.writeValueAsString(o), excludingFields));
    final var excludedNullFields = om.writeValueAsString(excludedFields);
    return sortJson(excludedNullFields);
  }

  @SneakyThrows
  public static String readSortDonWriteNullsAndExcludeFields(Path path, String... excludingFields) {
    final var json = dontWriteNonNullObjectMapper().readValue(path.toFile(), JsonNode.class);
    stripNulls(json);
    return readSortDonWriteNullsAndExcludeFields(json, excludingFields);
  }

  @SneakyThrows
  public static String readSortDonWriteNullsAndExcludeFields(String path, String... excludingFields) {
    final var om = dontWriteNonNullObjectMapper();
    return sortJson(om.readTree(sortJsonExcluding(readString(path), excludingFields)));
  }

  @SneakyThrows
  public static String readAndSortJsonExcluding(String path, String... excludingFields) {
    return sortJsonExcluding(readString(path), excludingFields);
  }

  @SneakyThrows
  public static String sortJson(Object o) {
    return sortJson(objectMapper.writeValueAsString(o));
  }

  @SneakyThrows
  public static String sortJson(String json) {
    return objectMapper.writeValueAsString(objectMapper.treeToValue(objectMapper.readTree(json), Object.class));
  }

  @SneakyThrows
  public static String sortJsonExcluding(Object o, String ... excludingFields) {
    return sortJsonExcluding(JsonUtils.writeValueAsString(o), excludingFields);
  }

  @SneakyThrows
  public static String sortJsonExcluding(String json, String ... excludingFields) {
    final var tree = (ObjectNode) JsonUtils.readTree(json);
    for (String field : excludingFields) {
      tree.remove(field);
    }
    return objectMapper.writeValueAsString(objectMapper.treeToValue(tree, Object.class));
  }

  @SneakyThrows
  public static InputStream readAsStream(String path) {
    return TestUtils.class.getResourceAsStream(path);
  }

  @SneakyThrows
  public static Path readResource(String path) {
    final var f = TestUtils.class
      .getResource(path)
      .getFile();
    return Paths.get(f);
  }

  /**
   * Refactoring from mockito 3.4  to 5.0 looking at
   * https://github.com/mockito/mockito/blob/v3.4.8/src/main/java/org/mockito/internal/util/reflection/Fields.java
   */
  @SneakyThrows
  public static void resetMocks(Object jUnitInstance) {
    for (final Field field : FieldUtils.getAllFields(jUnitInstance.getClass())) {
      final var v = FieldUtils.readField(field, jUnitInstance, true);
      if (MockUtil.isMock(v)) {
        try {
          Mockito.reset(v);
        } catch (Throwable t) {
        }
      }
    }
  }

  @SneakyThrows
  public static String readString(Path path) {
    return Files.readString(path);
  }

  private static ObjectMapper dontWriteNonNullObjectMapper() {
    return new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .registerModule(new JavaTimeModule())
      .enable(SerializationFeature.INDENT_OUTPUT)
      ;
  }

  public static void stripNulls(JsonNode node) {
    Iterator<JsonNode> it = node.iterator();
    while (it.hasNext()) {
      JsonNode child = it.next();
      if (child.isNull())
        it.remove();
      else
        stripNulls(child);
    }
  }

  @SneakyThrows
  public static String readAsStringAndExcludeNullFields(Path path) {
    final var in = Files.newInputStream(path);
    try(in){
      final var tree = JsonUtils.readTree(in);
      stripNulls(tree);
      return tree.toPrettyString();
    }
  }
}
