package com.mageddo.commons.lang;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Singletons {

  private static final Map<String, Object> store = new ConcurrentHashMap<>();

  private Singletons() {
  }

  public static <T> T createOrGet(Class<?> clazz, Supplier<T> sup) {
    return createOrGet(clazz.getName(), sup);
  }

  public static <T> T createOrGet(String key, Supplier<T> sup) {
    return (T) store.computeIfAbsent(key, _k -> sup.get());
  }
// https://stackoverflow.com/questions/15156840/singleton-class-with-several-different-classloaders
//  public static <T> T createOrGet(String key, Supplier<T> sup) {
//    // There should be just one system class loader object in the whole JVM.
//    synchronized (ClassLoader.getSystemClassLoader()) {
//      Properties sysProps = System.getProperties();
//      // The key is a String, because the .class object would be different across classloaders.
//      T cached = (T) sysProps.get(key);
//
//      // Some other class loader loaded JvmWideSingleton earlier.
//      if (cached != null) {
//        return cached;
//      } else {
//        // Otherwise this classloader is the first one, let's create a singleton.
//        // Make sure not to do any locking within this.
//        final var hotload = sup.get();
//        System.getProperties().put(key, hotload);
//        return hotload;
//      }
//    }
//  }

  public static <T> T get(Class<?> clazz) {
    return get(clazz.getName());
  }

  public static <T> T get(String key) {
    return (T) System
      .getProperties()
      .get(key);
  }

  public static void clear(Class<?> clazz) {
    clear(clazz.getName());
  }

  public static void clear(String key) {
    System.getProperties().remove(key);
  }
}
