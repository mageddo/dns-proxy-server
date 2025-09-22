package com.mageddo.dataformat.env;

import com.mageddo.json.JsonUtils;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class EnvMapper {

  private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(.+)_([0-9]+)$");
  private static final Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");

  public String toJson(Map<String, String> env, String varsPrefix) {
    final var root = new LinkedHashMap<String, Object>();
    env.entrySet()
      .stream()
      .filter(entry -> entry.getKey() != null && entry.getKey().startsWith(varsPrefix))
      .sorted(Map.Entry.comparingByKey())
      .forEach(entry -> this.insert(root, entry.getKey().substring(varsPrefix.length()), entry.getValue()));
    return JsonUtils.writeValueAsString(root);
  }

  @SuppressWarnings("unchecked")
  private void insert(Map<String, Object> root, String rawKey, String rawValue) {
    final var segments = this.parseSegments(rawKey);
    var current = root;
    for (var index = 0; index < segments.size(); index++) {
      final var segment = segments.get(index);
      final var last = index == segments.size() - 1;
      if (segment.hasIndex()) {
        final var list = this.getOrCreateList(current, segment.name());
        this.ensureSize(list, segment.index());
        if (last) {
          list.set(segment.index(), this.convertValue(rawValue));
        } else {
          final var next = list.get(segment.index());
          if (next instanceof Map) {
            current = (Map<String, Object>) next;
          } else {
            final var newMap = new LinkedHashMap<String, Object>();
            list.set(segment.index(), newMap);
            current = newMap;
          }
        }
      } else {
        if (last) {
          current.put(segment.name(), this.convertValue(rawValue));
        } else {
          final var next = current.get(segment.name());
          if (next instanceof Map) {
            current = (Map<String, Object>) next;
          } else {
            final var newMap = new LinkedHashMap<String, Object>();
            current.put(segment.name(), newMap);
            current = newMap;
          }
        }
      }
    }
  }

  private List<PathSegment> parseSegments(String rawKey) {
    final var segments = new ArrayList<PathSegment>();
    final var keys = rawKey.split("__");
    for (final var key : keys) {
      segments.add(this.parseSegment(key));
    }
    return segments;
  }

  private PathSegment parseSegment(String segment) {
    final Matcher matcher = ARRAY_INDEX_PATTERN.matcher(segment);
    if (matcher.matches()) {
      final var property = this.toCamelCase(matcher.group(1));
      final var index = Integer.parseInt(matcher.group(2));
      return new PathSegment(property, index);
    }
    return new PathSegment(this.toCamelCase(segment), null);
  }

  private List<Object> getOrCreateList(Map<String, Object> current, String key) {
    final var existing = current.get(key);
    if (existing instanceof List) {
      return (List<Object>) existing;
    }
    final var list = new ArrayList<Object>();
    current.put(key, list);
    return list;
  }

  private void ensureSize(List<Object> list, int index) {
    while (list.size() <= index) {
      list.add(null);
    }
  }

  private Object convertValue(String rawValue) {
    if (rawValue == null) {
      return null;
    }
    final var value = rawValue.trim();
    if (value.isEmpty()) {
      return "";
    }
    if ("null".equalsIgnoreCase(value)) {
      return null;
    }
    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
      return Boolean.valueOf(value);
    }
    if (INTEGER_PATTERN.matcher(value).matches()) {
      try {
        return Integer.valueOf(value);
      } catch (NumberFormatException e) {
        return Long.valueOf(value);
      }
    }
    return rawValue;
  }

  private String toCamelCase(String value) {
    final var lower = value.toLowerCase(Locale.ROOT);
    final var tokens = lower.split("_");
    final var builder = new StringBuilder();
    for (var index = 0; index < tokens.length; index++) {
      final var token = tokens[index];
      if (token.isEmpty()) {
        continue;
      }
      if (index == 0) {
        builder.append(token);
      } else {
        builder.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
      }
    }
    return builder.toString();
  }

  private static final class PathSegment {
    private final String name;
    private final Integer index;

    PathSegment(String name, Integer index) {
      this.name = name;
      this.index = index;
    }

    String name() {
      return this.name;
    }

    Integer index() {
      return this.index;
    }

    boolean hasIndex() {
      return this.index != null;
    }
  }
}
