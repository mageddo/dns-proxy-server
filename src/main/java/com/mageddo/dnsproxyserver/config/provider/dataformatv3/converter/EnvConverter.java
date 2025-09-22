package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class EnvConverter implements Converter {

  static final String PREFIX = "DPS_";

  private final Map<String, String> environment;

  public EnvConverter() {
    this(System.getenv());
  }

  EnvConverter(Map<String, String> environment) {
    this.environment = new HashMap<>(environment);
  }

  @Override
  public ConfigV3 parse() {
    return new Parser(this.environment).parse();
  }

  @Override
  public String serialize(ConfigV3 config) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public int priority() {
    return 0;
  }

  private static final class Parser {

    private static final Map<Class<?>, Map<String, Field>> CACHE = new ConcurrentHashMap<>();
    public static final String FIELD_AND_CASE_SEPARATOR = "_";

    private final Map<String, String> environment;

    private Parser(Map<String, String> environment) {
      this.environment = environment;
    }

    private ConfigV3 parse() {
      final var config = new ConfigV3();
      this.filterMatchingVariables()
          .forEach(entry -> this.apply(config, entry.getKey(), entry.getValue()));
      return config;
    }

    private Stream<Map.Entry<String, String>> filterMatchingVariables() {
      return this.environment
        .entrySet()
        .stream()
        .filter(entry -> isTargetVariable(entry.getKey(), entry.getValue()));
    }

    private static boolean isTargetVariable(String key, String value) {
      return key != null && key.startsWith(PREFIX) && value != null;
    }

    private void apply(ConfigV3 config, String key, String value) {
      final var tokens = this.buildTokens(key);
      this.setValue(config, ConfigV3.class, tokens, value);
    }

    private Deque<String> buildTokens(String key) {
      final var rawPath = key.substring(PREFIX.length());
      return new ArrayDeque<>(List.of(rawPath.split(FIELD_AND_CASE_SEPARATOR)));
    }

    private void setValue(Object current, Class<?> type, Deque<String> tokens, String value) {
      final var match = this.matchField(type, tokens);
      consume(tokens, match.segmentsConsumed());

      try {
        final var field = match.field();
        final var fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)) {
          setListValue(current, field, tokens, value);
          return;
        }

        if (tokens.isEmpty()) {
          final var convertedValue = convert(value, fieldType);
          field.set(current, convertedValue);
          return;
        }

        var nestedValue = field.get(current);
        if (nestedValue == null) {
          nestedValue = instantiate(fieldType);
          field.set(current, nestedValue);
        }
        setValue(nestedValue, fieldType, tokens, value);
      } catch (ReflectiveOperationException e) {
        throw new IllegalStateException("Failed to set value for key: " + match.field()
                                                                               .getName(), e);
      }
    }

    private void setListValue(Object current, Field field, Deque<String> tokens, String value)
      throws ReflectiveOperationException {

      if (tokens.isEmpty()) {
        throw new IllegalArgumentException("Missing list index for field " + field.getName());
      }

      final var index = Integer.parseInt(tokens.removeFirst());
      final var list = ensureList(current, field);
      ensureCapacity(list, index);

      final var elementType = resolveListElementType(field);
      if (tokens.isEmpty()) {
        list.set(index, convert(value, elementType));
        return;
      }

      var item = list.get(index);
      if (item == null) {
        item = instantiate(elementType);
        list.set(index, item);
      }
      setValue(item, elementType, tokens, value);
    }

    private static List<Object> ensureList(Object current, Field field) throws IllegalAccessException {
      @SuppressWarnings("unchecked") var list = (List<Object>) field.get(current);
      if (list == null) {
        list = new ArrayList<>();
        field.set(current, list);
      }
      return list;
    }

    private static void ensureCapacity(List<Object> list, int index) {
      while (list.size() <= index) {
        list.add(null);
      }
    }

    private static Object convert(String value, Class<?> targetType) {
      if (Objects.equals(targetType, String.class)) {
        return value;
      }
      if (Objects.equals(targetType, Integer.class) || targetType == int.class) {
        return Integer.valueOf(value);
      }
      if (Objects.equals(targetType, Boolean.class) || targetType == boolean.class) {
        return Boolean.valueOf(value);
      }
      if (Objects.equals(targetType, Long.class) || targetType == long.class) {
        return Long.valueOf(value);
      }
      throw new IllegalArgumentException("Unsupported conversion to " + targetType);
    }

    private static Object instantiate(Class<?> type) {
      try {
        return type.getDeclaredConstructor()
                   .newInstance();
      } catch (ReflectiveOperationException e) {
        throw new IllegalStateException("Could not instantiate " + type.getName(), e);
      }
    }

    private FieldMatch matchField(Class<?> type, Deque<String> tokens) {
      final var fields = CACHE.computeIfAbsent(type, Parser::loadFields);
      final var iterator = tokens.iterator();
      final var consumed = new ArrayList<String>();
      final var builder = new StringBuilder();

      while (iterator.hasNext()) {
        final var segment = iterator.next();
        if (!consumed.isEmpty()) {
          builder.append(FIELD_AND_CASE_SEPARATOR);
        }
        builder.append(segment);
        consumed.add(segment);
        final var field = fields.get(builder.toString());
        if (field != null) {
          return new FieldMatch(field, consumed.size());
        }
      }
      throw new IllegalArgumentException("Unknown configuration path: " + String.join(FIELD_AND_CASE_SEPARATOR, tokens));
    }

    private static Map<String, Field> loadFields(Class<?> type) {
      final var fields = new HashMap<String, Field>();
      for (Field field : type.getFields()) {
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        field.setAccessible(true);
        fields.put(toEnvKey(field.getName()), field);
      }
      return fields;
    }

    private static String toEnvKey(String name) {
      final var builder = new StringBuilder();
      for (int i = 0; i < name.length(); i++) {
        final var ch = name.charAt(i);
        if (Character.isUpperCase(ch)) {
          builder.append('_');
        }
        builder.append(Character.toUpperCase(ch));
      }
      return builder.toString();
    }

    private static void consume(Deque<String> tokens, int items) {
      for (int i = 0; i < items; i++) {
        tokens.removeFirst();
      }
    }

    private static Class<?> resolveListElementType(Field field) {
      final var type = field.getGenericType();
      if (type instanceof ParameterizedType parameterizedType) {
        final var actualType = parameterizedType.getActualTypeArguments()[0];
        return extractClass(actualType);
      }
      throw new IllegalArgumentException("Unable to resolve list element type for field " + field.getName());
    }

    private static Class<?> extractClass(Type type) {
      if (type instanceof Class<?> clazz) {
        return clazz;
      }
      if (type instanceof ParameterizedType parameterizedType) {
        return extractClass(parameterizedType.getRawType());
      }
      throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private record FieldMatch(Field field, int segmentsConsumed) {
    }
  }
}
