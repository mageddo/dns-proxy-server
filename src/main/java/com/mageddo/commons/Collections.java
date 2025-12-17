package com.mageddo.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;

public class Collections {
  public static <T> List<T> newListAdding(List<T> tags, T... items) {

    tags = ObjectUtils.firstNonNull(tags, new ArrayList<>());

    final var list = new ArrayList<>(tags);
    list.addAll(Arrays.asList(items));
    return list;
  }

  public static <T> T first(Collection<T> c) {
    if (c == null || c.isEmpty()) {
      return null;
    }
    return c.stream()
        .findFirst()
        .orElse(null);
  }

  public static <T> T last(Collection<T> c) {
    if (c == null || c.isEmpty()) {
      return null;
    }
    return c.stream()
        .skip(c.size() - 1)
        .findFirst()
        .orElse(null);
  }


  public static <T> T get(List<T> c, int i) {
    if (c == null || i < 0 || c.size() <= i) {
      return null;
    }
    return c.get(i);
  }

  public static <From, To> Set<To> map(Set<From> source, Function<From, To> mapper) {
    return mapStream(source, mapper)
        .collect(Collectors.toSet());
  }

  public static <From, To> List<To> mapToList(Set<From> source, Function<From, To> mapper) {
    return mapStream(source, mapper)
        .toList();
  }

  public static <From, To> List<To> map(From[] source, Function<From, To> mapper) {
    return mapStream(Stream.of(source), mapper)
        .toList();
  }

  public static <From, To> List<To> map(Collection<From> source, Function<From, To> mapper) {
    final var r = mapStream(source, mapper);
    if (r == null) {
      return null;
    }
    return r.toList();
  }


  public static <From, To> List<To> flatMap(
      Collection<From> source, Function<From, List<To>> mapper
  ) {
    return source.stream()
        .flatMap(mapper.andThen(Collection::stream))
        .toList();
  }

  public static <From, To> List<To> mapSorting(
      List<From> source, Function<From, To> mapper, Comparator<To> comparator
  ) {
    return mapStream(source, mapper)
        .sorted(comparator)
        .toList();
  }

  public static <From, To> Stream<To> mapStream(
      Collection<From> source, Function<From, To> mapper
  ) {
    if (source == null) {
      return null;
    }
    return mapStream(source.stream(), mapper);
  }

  public static <From, To> Stream<To> mapStream(
      Stream<From> source, Function<From, To> mapper
  ) {
    if (source == null) {
      return null;
    }
    return source
        .map(it -> {
          try {
            return mapper.apply(it);
          } catch (Throwable e) {
            throw new IllegalArgumentException("Can't map: " + it, e);
          }
        });
  }

  public static <From, To> List<To> mapNonNulls(Collection<From> source,
      Function<From, To> mapper) {
    return mapStream(source, mapper)
        .filter(Objects::nonNull)
        .toList();
  }

  public static int size(Collection<?> collection) {
    return collection == null ? 0 : collection.size();
  }

  public static <T> List<T> emptyList() {
    return java.util.Collections.emptyList();
  }

  public static boolean isNotEmpty(Collection<?> c) {
    return !isEmptyOrNull(c);
  }

  public static <T> boolean isEmpty(Collection<T> c) {
    return c.isEmpty();
  }

  public static boolean isEmptyOrNull(Collection<?> c) {
    return c == null || c.isEmpty();
  }

  /**
   * Same behavior as SQL UNION
   */
  @SafeVarargs
  public static <T> List<T> union(List<T>... collections) {
    return unionAll(collections).stream()
        .distinct()
        .toList();
  }

  public static <T> List<T> unionAll(List<T> a, List<T> b) {
    if (a.isEmpty()) {
      return b;
    } else if (b.isEmpty()) {
      return a;
    }
    return Collections.unionAll(a, b, Collections.emptyList());
  }

  /**
   * Same behavior as SQL UNION ALL
   */
  @SafeVarargs
  public static <T> List<T> unionAll(List<T>... collections) {
    if (collections == null || collections.length == 0) {
      return emptyList();
    }
    final var items = new ArrayList<T>(collections.length);
    for (final var c : collections) {
      items.addAll(c);
    }
    return items;
  }

  public static <T> List<T> reverse(List<T> c) {
    if (c == null) {
      return null;
    }
    final var nc = new ArrayList<>(c);
    java.util.Collections.reverse(nc);
    return nc;
  }

  public static <T> List<T> sort(Set<T> c, Comparator<T> comparator) {
    return sort(new ArrayList<>(c), comparator);
  }

  public static <T> List<T> sort(List<T> c, Comparator<T> comparator) {
    if (c == null) {
      return null;
    }
    if (isMutable(c)) {
      c.sort(comparator);
      return c;
    }
    final var nc = new ArrayList<>(c);
    nc.sort(comparator);
    return nc;
  }

  public static boolean isImmutable(Collection<?> c) {
    return !isMutable(c);
  }

  public static boolean isMutable(Collection<?> c) {
    return !(c.getClass()
        .getName()
        .contains("Unmodifiable")
        || c.getClass()
        .getName()
        .contains("Immutable")
        || c.getClass()
        .getName()
        .contains("FixedSize"));
  }

  public static <T extends Comparable<T>> Comparator<T> safeComparable() {
    return (a, b) -> {
      if (a == null && b == null) {
        return 0;
      }
      if (a == null || b == null) {
        return 0;
      }
      return a.compareTo(b);
    };
  }

  public static double[] mapToDoubleNativeArray(List<Integer> c) {
    return mapToDoubleNativeArray(c, Integer::doubleValue);
  }

  public static <T> double[] mapToDoubleNativeArray(List<T> c, Function<T, Double> mapper) {
    if (Collections.isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .mapToDouble(mapper::apply)
        .toArray();
  }

  public static <T> List<T> subtract(List<T> source, List<T> toSubtract) {
    final var sourceCopy = new ArrayList<>(source);
    sourceCopy.removeAll(toSubtract);
    return sourceCopy;
  }

  public static <T, K> List<T> subtract(
      List<T> source, List<T> toSub, Function<T, K> idMapper
  ) {
    final var store = keyBy(source, idMapper);
    for (final var el : toSub) {
      final var id = idMapper.apply(el);
      store.remove(id);
    }
    return new ArrayList<>(store.values());
  }

  public static <T> T findOne(List<T> c) {
    return findOne(c, null);
  }

  public static <T> T findZeroOrOne(List<T> c, Predicate<T> p, String msg, Object... args) {
    return findZeroOrOne(
        c.stream()
            .filter(p)
            .toList(),
        msg,
        args
    );
  }

  public static <T> T findZeroOrOne(List<T> c) {
    return findZeroOrOne(c, "");
  }

  public static <T> T findZeroOrOne(List<T> c, String msg, Object... args) {
    if (Collections.isEmptyOrNull(c)) {
      return null;
    }
    if (Collections.size(c) == 1) {
      return Collections.first(c);
    }
    final var finalMsg = String.format("invalid quantity returned, quantity=%d | ", c.size()) + msg;
    throw new IllegalStateException(String.format(finalMsg, args));
  }

  public static <T> T findFirst(List<T> c, Predicate<T> p) {
    if (c == null) {
      return null;
    }
    return c.stream()
        .filter(p)
        .findFirst()
        .orElse(null);
  }

  public static <T> T findOne(List<T> c, Predicate<T> p, Supplier<String> msg) {
    if (c == null) {
      return null;
    }
    final var results = c.stream()
        .filter(p)
        .toList();
    if (results.size() > 1) {
      throw new IllegalStateException(
          String.format("expected one but found %d, %s: %s", results.size(), results, msg.get())
      );
    }
    return first(results);
  }

  public static <T> T findFirstOrThrow(List<T> c, Predicate<T> p, String msg) {
    return Objects.requireNonNull(Collections.findFirst(c, p), msg);
  }

  public static <T> T findFirstOrThrow(List<T> c, Predicate<T> p, Supplier<String> msg) {
    return Objects.requireNonNull(Collections.findFirst(c, p), msg);
  }

  public static <T> T findOne(List<T> c, String msg, Object... args) {
    if (Collections.size(c) == 1) {
      return Collections.first(c);
    }
    final var finalMsg = String.format("invalid quantity returned, quantity=%d | ", c.size()) + msg;
    throw new IllegalStateException(String.format(finalMsg, args));
  }

  /**
   * @return Removed
   */
  public static <T> List<T> removeIf(List<T> c, Predicate<T> predicate) {
    final var toRemove = c.stream()
        .filter(predicate)
        .toList();
    c.removeAll(toRemove);
    return toRemove;
  }

  public static <T> T min(Collection<T> c, Comparator<T> comparator) {
    if (isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .min(comparator)
        .orElse(null);
  }

  public static <T> T max(Collection<T> c, Comparator<T> comparator) {
    if (isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .max(comparator)
        .orElse(null);
  }

  public static <T> int filterCounting(List<T> c, Predicate<T> predicate) {
    if (isEmptyOrNull(c)) {
      return 0;
    }
    var count = 0;
    for (final var el : c) {
      try {
        if (predicate.test(el)) {
          count++;
        }
      } catch (Exception e) {
        throw new IllegalArgumentException(
            String.format("failedToProcess, item=%s, msg=%s", el, e.getMessage()),
            e
        );
      }
    }
    return count;
  }

  public static <T> int filterCounting(T[] arr, Predicate<T> predicate) {
    if (isEmptyOrNull(arr)) {
      return 0;
    }
    var count = 0;
    for (final var e : arr) {
      if (predicate.test(e)) {
        count++;
      }
    }
    return count;
  }

  private static <T> boolean isEmptyOrNull(T[] arr) {
    return arr == null || arr.length == 0;
  }

  public static int count(Stream<?> c) {
    return Math.toIntExact(c.count());
  }

  public static <T, R> Optional<R> forEachReducing(
      Collection<T> c, Function<T, R> fn, BinaryOperator<R> reducer
  ) {
    return c.stream()
        .map(fn)
        .reduce(reducer);
  }

  public static <T> Optional<Integer> forEachSumming(List<T> c, Function<T, Integer> fn) {
    return forEachReducing(c, fn, Integer::sum);
  }

  public static <T> int forEachSummingOrZero(List<T> c, Function<T, Integer> fn) {
    return forEachSumming(c, fn).orElse(0);
  }

  public static <T, R> List<R> mapNonNullsDistinct(List<T> c, Function<T, R> fn) {
    return c
        .stream()
        .map(fn)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }

  public static <T, R> List<R> mapDelta(List<T> c, BiFunction<T, T, R> fn) {
    if (isEmptyOrNull(c)) {
      return Collections.emptyList();
    }
    final var r = new ArrayList<R>();
    for (var i = 1; i < c.size(); i++) {
      final var prev = c.get(i - 1);
      final var curr = c.get(i);
      final var apply = fn.apply(prev, curr);
      if (apply != null) {
        r.add(apply);
      }
    }
    return r;
  }

  public static <T, R> List<R> mapDistinct(List<T> c, Function<T, R> fn) {
    return c
        .stream()
        .map(fn)
        .distinct()
        .toList();
  }

  public static boolean isArrayOrCollection(Object o) {
    if (o == null) {
      return false;
    }
    return o.getClass()
        .isArray() || o instanceof Iterable;
  }

  public static <T> List<T> listOf(T first, List<T> secondary) {
    return unionAll(singletonList(first), secondary);
  }

  public static <Z, T> List<T> listOf(T first, List<Z> next, Function<Z, T> converter) {
    return listOf(first, map(next, converter));
  }

  public static <T> List<T> singletonList(T o) {
    return java.util.Collections.singletonList(o);
  }

  public static <T, K> List<T> groupAndReduce(
      List<T> c,
      Function<? super T, ? extends K> classifier,
      BinaryOperator<T> reducer
  ) {
    if (Collections.isEmptyOrNull(c)) {
      return Collections.emptyList();
    }
    return groupAndReduce(c.stream(), classifier, reducer);
  }

  public static <T, K> List<T> groupAndReduce(
      Stream<T> stream,
      Function<? super T, ? extends K> classifier,
      BinaryOperator<T> reducer
  ) {
    return stream
        .collect(Collectors.groupingBy(classifier))
        .values()
        .stream()
        .map(it -> Collections.reduceOrNull(it, reducer))
        .filter(Objects::nonNull)
        .toList();
  }

  public static <T> Optional<T> reduce(List<T> c, BinaryOperator<T> accumulator) {
    if (Collections.isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .reduce(accumulator);
  }

  public static <T> T reduceOrNull(List<T> c, BinaryOperator<T> accumulator) {
    if (Collections.isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .reduce(accumulator)
        .orElse(null);
  }

  public static <T, R> R reduceOrNull(
      List<T> c, Function<T, R> fn, BinaryOperator<R> accumulator
  ) {
    if (Collections.isEmptyOrNull(c)) {
      return null;
    }
    return c.stream()
        .map(fn)
        .reduce(accumulator)
        .orElse(null);
  }

  public static <T, R> R reduceFilteringOrNull(
      List<T> c,
      Predicate<T> predicate,
      Function<T, R> mappingFn, BinaryOperator<R> accumulator) {
    return reduceFiltering(c, predicate, mappingFn, accumulator).orElse(null);
  }

  public static <T, R> Optional<R> reduceFiltering(
      List<T> c,
      Predicate<T> predicate,
      Function<T, R> mappingFn, BinaryOperator<R> accumulator) {
    return c.stream()
        .filter(predicate)
        .map(mappingFn)
        .reduce(accumulator);
  }

  @SafeVarargs
  public static <T> List<T> listOf(T... elements) {
    return Stream.of(elements)
        .collect(Collectors.toCollection(ArrayList::new))
        ;
  }

  public static <T> boolean indexedContains(List<? extends Comparable<? super T>> c, T e) {
    return java.util.Collections.binarySearch(c, e) > 0;
  }

  public static <T> List<T> removeLast(List<T> c) {
    if (Collections.isEmptyOrNull(c)) {
      return c;
    }
    c.remove(c.size() - 1);
    return c;
  }

  public static <T> Set<T> emptySet() {
    return java.util.Collections.emptySet();
  }

  public static <T, R> int countDistinctBy(Collection<T> c, Function<T, R> fn) {
    return Math.toIntExact(c.stream()
        .map(fn)
        .distinct()
        .count()
    );
  }

  public static <T, R> Map<R, Long> countBy(Collection<T> c, Function<T, R> fn) {
    return c.stream()
        .collect(Collectors.groupingBy(
            fn,
            Collectors.counting()
        ));
  }

  public static <T, R> Map<R, T> keyBy(Collection<T> c, Function<T, R> fn) {
    if (c == null) {
      return null;
    }
    if (c.isEmpty()) {
      return new HashMap<>();
    }
    return c.stream()
        .collect(Collectors.toMap(fn, Function.identity()));
  }

  public static <T> Set<T> subtract(Set<T> c, Set<T> toRemove) {
    if (isEmptyOrNull(c)) {
      return c;
    }
    toRemove.forEach(c::remove);
    return c;
  }

  @SafeVarargs
  public static <T> Set<T> subtract(Set<T> source, T... toExclude) {
    if (isEmptyOrNull(source)) {
      return source;
    }
    for (var exclude : toExclude) {
      source.remove(exclude);
    }
    return source;
  }

  public static <T> List<T> toList(Set<T> c) {
    if (c == null) {
      return null;
    }
    if (isEmptyOrNull(c)) {
      return Collections.emptyList();
    }
    return c.stream()
        .toList();
  }

  public static <T> Set<T> toSet(Collection<T> c) {
    if (c == null) {
      return null;
    }
    if (c.isEmpty()) {
      return Collections.emptySet();
    }
    return new HashSet<>(c);
  }

  @SafeVarargs
  public static <T> List<T> toList(T... arr) {
    if (arr == null) {
      return null;
    }
    return List.of(arr);
  }

  public static <T> boolean containsAnyOf(List<T> c, List<T> wanted) {
    if (isEmptyOrNull(c) || isEmptyOrNull(wanted)) {
      return false;
    }
    final var set = toSet(c);
    for (final var e : wanted) {
      if (set.contains(e)) {
        return true;
      }
    }
    return false;
  }

  public static <T> boolean contains(List<T> c, T wanted) {
    if (isEmptyOrNull(c)) {
      return false;
    }
    return toSet(c).contains(wanted);
  }

  @SafeVarargs
  public static <T> List<T> filter(List<T> c, Predicate<T>... predicates) {
    if (isEmptyOrNull(c)) {
      return c;
    }
    var stream = c.stream();
    for (var predicate : predicates) {
      stream = stream.filter(predicate);
    }
    return stream.toList();
  }

  public static <T, R> Set<R> mapToSet(Collection<T> c, Function<T, R> fn) {
    if (c == null) {
      return null;
    }
    if (c.isEmpty()) {
      return Collections.emptySet();
    }
    return c.stream()
        .map(fn)
        .collect(Collectors.toSet());
  }

  public static <K, V> Map<K, List<V>> keyBy(Iterable<V> itr, Function<V, K> keyFn) {
    if (itr == null) {
      return null;
    }
    final var m = new LinkedHashMap<K, List<V>>();
    itr.forEach(v -> {
      final var key = keyFn.apply(v);
      m.computeIfAbsent(key, o -> new ArrayList<>());
      m.get(key)
          .add(v);
    });
    return m;
  }

  public static <K, V> Map<K, List<V>> keyByOrdered(List<V> c, Function<V, K> keyFn) {
    if (c == null) {
      return null;
    }
    return c.stream()
        .collect(Collectors.groupingBy(
            keyFn, LinkedHashMap::new, Collectors.toList()
        ));
  }

  public static <T> int forEachCount(Collection<T> collection, Consumer<T> c) {
    collection.forEach(c);
    return collection.size();
  }

  public static <T> boolean allMatch(List<T> c, Predicate<T> p) {
    return c.stream()
        .allMatch(p);
  }

  public static <T> boolean anyMatch(List<T> c, Predicate<T> p) {
    return c.stream()
        .anyMatch(p);
  }

  public static double[] mapToSortedDoubleNativeArray(Long[] c) {
    return Stream.of(c)
        .mapToDouble(Double::valueOf)
        .sorted()
        .toArray();
  }

  public static double[] mapToSortedDoubleNativeArray(Collection<Long> c) {
    return c
        .stream()
        .mapToDouble(Double::valueOf)
        .sorted()
        .toArray();
  }

}
