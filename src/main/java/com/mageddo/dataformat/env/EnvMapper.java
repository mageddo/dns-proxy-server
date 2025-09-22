package com.mageddo.dataformat.env;

import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.json.JsonUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class EnvMapper {

  private static final String SEGMENT_SEPARATOR = "__";
  private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(.+)_([0-9]+)$");
  private static final Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");

  private final SegmentMapper segmentMapper =  new SegmentMapper();

  public String toJson(final Map<String, String> env, final String varsPrefix) {
    final var root = new LinkedHashMap<String, Object>();

    this.findMatchingEnvs(env, varsPrefix)
        .forEach(e -> insertPropertyAt(root, e, varsPrefix));

    return JsonUtils.writeValueAsString(root);
  }

  private void insertPropertyAt(
    Map<String, Object> root,
    Map.Entry<String, String> entry,
    String varsPrefix
  ) {
    this.insert(root, buildEnvWithoutPrefix(entry.getKey(), varsPrefix), entry.getValue());
  }

  private static String buildEnvWithoutPrefix(final String key, String prefix) {
    return key.substring(prefix.length());
  }

  private Stream<Map.Entry<String, String>> findMatchingEnvs(Map<String, String> env, String varsPrefix) {
    return env.entrySet()
              .stream()
              .filter(e -> e.getKey() != null && e.getKey()
                                                  .startsWith(varsPrefix))
              .sorted(Map.Entry.comparingByKey());
  }

  @SuppressWarnings("unchecked")
  private void insert(final Map<String, Object> root, final String rawKey, final String rawValue) {

    final var segments = this.segmentMapper.ofRawKey(rawKey);
    var current = root;

    for (var i = 0; i < segments.size(); i++) {
      final var seg = segments.get(i);
      final var isLast = (i == segments.size() - 1);

      if (seg.hasIndex()) {
        final var list = getOrCreateList(current, seg.name());
        ensureSize(list, seg.index());

        if (isLast) {
          list.set(seg.index(), convertValue(rawValue));
          return;
        }

        final var next = list.get(seg.index());
        if (next instanceof Map) {
          current = (Map<String, Object>) next;
        } else {
          final var newMap = new LinkedHashMap<String, Object>();
          list.set(seg.index(), newMap);
          current = newMap;
        }
        continue;
      }

      if (isLast) {
        current.put(seg.name(), convertValue(rawValue));
        return;
      }

      final var next = current.get(seg.name());
      if (next instanceof Map) {
        current = (Map<String, Object>) next;
      } else {
        final var newMap = new LinkedHashMap<String, Object>();
        current.put(seg.name(), newMap);
        current = newMap;
      }
    }
  }

  private static class SegmentMapper {

    private List<PathSegment> ofRawKey(final String rawKey) {
      final var segments = new ArrayList<PathSegment>();
      for (final var token : rawKey.split(SEGMENT_SEPARATOR)) {
        segments.add(this.parseSegment(token));
      }
      return segments;
    }

    private PathSegment parseSegment(final String segment) {
      final var m = ARRAY_INDEX_PATTERN.matcher(segment);
      if (m.matches()) {
        final var name = this.toCamelCase(m.group(1));
        final var index = Integer.parseInt(m.group(2));
        return new PathSegment(name, index);
      }
      return new PathSegment(this.toCamelCase(segment), null);
    }

    private String toCamelCase(String value) {
      return CaseUtils.toCamelCase(StringUtils.lowerCase(value), false, '_');
    }

    /**
     * Ex.: "servers_0" => name=servers, index=0
     * "solver__remote__dnsServers_1" é segmentado por "__"
     */
    record PathSegment(String name, Integer index) {
      boolean hasIndex() {
        return this.index != null;
      }
    }
  }


  @SuppressWarnings("unchecked")
  private List<Object> getOrCreateList(final Map<String, Object> current, final String key) {
    final var existing = current.get(key);
    if (existing instanceof List) {
      return (List<Object>) existing;
    }
    final var list = new ArrayList<>();
    current.put(key, list);
    return list;
  }

  private void ensureSize(final List<Object> list, final int index) {
    while (list.size() <= index) {
      list.add(null);
    }
  }

  private Object convertValue(final String rawValue) {
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

    if (isInteger(value)) {
      try {
        final var asLong = Long.parseLong(value);
        if (Numbers.canBeInt(asLong)) {
          return (int) asLong;
        }
        return asLong;
      } catch (NumberFormatException ignore) {
        // não deve ocorrer por causa do regex, mas mantemos seguro
      }
    }
    return value;
  }

  private static boolean isInteger(String value) {
    return INTEGER_PATTERN
      .matcher(value)
      .matches();
  }

}
