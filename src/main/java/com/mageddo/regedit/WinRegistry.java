//package com.mageddo.regedit;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.prefs.Preferences;
//
///**
// * https://github.com/apache/npanday/tree/6eed1c083efe68e73f6279715d1dda905ce97499/components/dotnet-registry/src/main/java/npanday/registry
// * https://stackoverflow.com/a/6163701/2979435
// */
//public class WinRegistry {
//
//  public static final int HKEY_CURRENT_USER = 0x80000001;
//  public static final int HKEY_LOCAL_MACHINE = 0x80000002;
//  public static final int REG_SUCCESS = 0;
//  public static final int REG_NOTFOUND = 2;
//  public static final int REG_ACCESSDENIED = 5;
//
//  private static final int KEY_ALL_ACCESS = 0xf003f;
//  private static final int KEY_READ = 0x20019;
//  private static final Preferences userRoot = Preferences.userRoot();
//  private static final Preferences systemRoot = Preferences.systemRoot();
//  private static final Class<? extends Preferences> userClass = userRoot.getClass();
//  private static final Method regOpenKey;
//  private static final Method regCloseKey;
//  private static final Method regQueryValueEx;
//  private static final Method regEnumValue;
//  private static final Method regQueryInfoKey;
//  private static final Method regEnumKeyEx;
//  private static final Method regCreateKeyEx;
//  private static final Method regSetValueEx;
//  private static final Method regDeleteKey;
//  private static final Method regDeleteValue;
//
//  static {
//    try {
//      /**
//       * jdk 17: https://github.com/openjdk/jdk/blob/9b10c69475dc493c64c19c78502c698a575fe7da/src/java.prefs/windows/classes/java/util/prefs/WindowsPreferences.java#L180
//       * jdk 8: https://github.com/openjdk/jdk/blob/9a9add8825a040565051a09010b29b099c2e7d49/jdk/src/windows/classes/java/util/prefs/WindowsPreferences.java#L140
//       */
//      regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
//          new Class[]{int.class, byte[].class, int.class});
//      regOpenKey.setAccessible(true);
//      regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey",
//          new Class[]{int.class});
//      regCloseKey.setAccessible(true);
//      regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx",
//          new Class[]{int.class, byte[].class});
//      regQueryValueEx.setAccessible(true);
//      regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue",
//          new Class[]{int.class, int.class, int.class});
//      regEnumValue.setAccessible(true);
//      regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1",
//          new Class[]{int.class});
//      regQueryInfoKey.setAccessible(true);
//      regEnumKeyEx = userClass.getDeclaredMethod(
//          "WindowsRegEnumKeyEx", new Class[]{int.class, int.class,
//              int.class});
//      regEnumKeyEx.setAccessible(true);
//      regCreateKeyEx = userClass.getDeclaredMethod(
//          "WindowsRegCreateKeyEx", new Class[]{int.class,
//              byte[].class});
//      regCreateKeyEx.setAccessible(true);
//      regSetValueEx = userClass.getDeclaredMethod(
//          "WindowsRegSetValueEx", new Class[]{int.class,
//              byte[].class, byte[].class});
//      regSetValueEx.setAccessible(true);
//      regDeleteValue = userClass.getDeclaredMethod(
//          "WindowsRegDeleteValue", new Class[]{int.class,
//              byte[].class});
//      regDeleteValue.setAccessible(true);
//      regDeleteKey = userClass.getDeclaredMethod(
//          "WindowsRegDeleteKey", new Class[]{int.class,
//              byte[].class});
//      regDeleteKey.setAccessible(true);
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  private WinRegistry() {
//  }
//
//  /**
//   * Read a value from key and value name
//   *
//   * @param hkey      HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
//   * @param key
//   * @param valueName
//   * @return the value
//   */
//  public static String readString(int hkey, String key, String valueName) {
//    try {
//      if (hkey == HKEY_LOCAL_MACHINE) {
//        return readString(systemRoot, hkey, key, valueName);
//      } else if (hkey == HKEY_CURRENT_USER) {
//        return readString(userRoot, hkey, key, valueName);
//      } else {
//        throw new IllegalArgumentException("hkey=" + hkey);
//      }
//    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  /**
//   * Read value(s) and value name(s) form given key
//   *
//   * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
//   * @param key
//   * @return the value name(s) plus the value(s)
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static Map<String, String> readStringValues(int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      return readStringValues(systemRoot, hkey, key);
//    } else if (hkey == HKEY_CURRENT_USER) {
//      return readStringValues(userRoot, hkey, key);
//    } else {
//      throw new IllegalArgumentException("hkey=" + hkey);
//    }
//  }
//
//  /**
//   * Read the value name(s) from a given key
//   *
//   * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
//   * @param key
//   * @return the value name(s)
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static List<String> readStringSubKeys(int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      return readStringSubKeys(systemRoot, hkey, key);
//    } else if (hkey == HKEY_CURRENT_USER) {
//      return readStringSubKeys(userRoot, hkey, key);
//    } else {
//      throw new IllegalArgumentException("hkey=" + hkey);
//    }
//  }
//
//  /**
//   * Create a key
//   *
//   * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
//   * @param key
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static void createKey(int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int[] ret;
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      ret = createKey(systemRoot, hkey, key);
//      regCloseKey.invoke(systemRoot, new Object[]{new Integer(ret[0])});
//    } else if (hkey == HKEY_CURRENT_USER) {
//      ret = createKey(userRoot, hkey, key);
//      regCloseKey.invoke(userRoot, new Object[]{new Integer(ret[0])});
//    } else {
//      throw new IllegalArgumentException("hkey=" + hkey);
//    }
//    if (ret[1] != REG_SUCCESS) {
//      throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
//    }
//  }
//
//  /**
//   * Write a value in a given key/value name
//   *
//   * @param hkey
//   * @param key
//   * @param valueName
//   * @param value
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static void writeStringValue
//  (int hkey, String key, String valueName, String value)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      writeStringValue(systemRoot, hkey, key, valueName, value);
//    } else if (hkey == HKEY_CURRENT_USER) {
//      writeStringValue(userRoot, hkey, key, valueName, value);
//    } else {
//      throw new IllegalArgumentException("hkey=" + hkey);
//    }
//  }
//
//  /**
//   * Delete a given key
//   *
//   * @param hkey
//   * @param key
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static void deleteKey(int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int rc = -1;
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      rc = deleteKey(systemRoot, hkey, key);
//    } else if (hkey == HKEY_CURRENT_USER) {
//      rc = deleteKey(userRoot, hkey, key);
//    }
//    if (rc != REG_SUCCESS) {
//      throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
//    }
//  }
//
//  /**
//   * delete a value from a given key/value name
//   *
//   * @param hkey
//   * @param key
//   * @param value
//   * @throws IllegalArgumentException
//   * @throws IllegalAccessException
//   * @throws InvocationTargetException
//   */
//  public static void deleteValue(int hkey, String key, String value)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int rc = -1;
//    if (hkey == HKEY_LOCAL_MACHINE) {
//      rc = deleteValue(systemRoot, hkey, key, value);
//    } else if (hkey == HKEY_CURRENT_USER) {
//      rc = deleteValue(userRoot, hkey, key, value);
//    }
//    if (rc != REG_SUCCESS) {
//      throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
//    }
//  }
//
//  // =====================
//
//  private static int deleteValue
//      (Preferences root, int hkey, String key, String value)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
//        new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
//    if (handles[1] != REG_SUCCESS) {
//      return handles[1];  // can be REG_NOTFOUND, REG_ACCESSDENIED
//    }
//    int rc = ((Integer) regDeleteValue.invoke(root,
//        new Object[]{
//            new Integer(handles[0]), toCstr(value)
//        })).intValue();
//    regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
//    return rc;
//  }
//
//  private static int deleteKey(Preferences root, int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int rc = ((Integer) regDeleteKey.invoke(root,
//        new Object[]{new Integer(hkey), toCstr(key)})).intValue();
//    return rc;  // can REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
//  }
//
//  private static String readString(Preferences root, int hkey, String key, String value)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
//        new Integer(hkey), toCstr(key), new Integer(KEY_READ)});
//    if (handles[1] != REG_SUCCESS) {
//      return null;
//    }
//    byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[]{
//        new Integer(handles[0]), toCstr(value)});
//    regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
//    return (valb != null ? new String(valb).trim() : null);
//  }
//
//  private static Map<String, String> readStringValues
//      (Preferences root, int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    HashMap<String, String> results = new HashMap<String, String>();
//    int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
//        new Integer(hkey), toCstr(key), new Integer(KEY_READ)});
//    if (handles[1] != REG_SUCCESS) {
//      return null;
//    }
//    int[] info = (int[]) regQueryInfoKey.invoke(root,
//        new Object[]{new Integer(handles[0])});
//
//    int count = info[0]; // count
//    int maxlen = info[3]; // value length max
//    for (int index = 0; index < count; index++) {
//      byte[] name = (byte[]) regEnumValue.invoke(root, new Object[]{
//          new Integer
//              (handles[0]), new Integer(index), new Integer(maxlen + 1)});
//      String value = readString(hkey, key, new String(name));
//      results.put(new String(name).trim(), value);
//    }
//    regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
//    return results;
//  }
//
//  private static List<String> readStringSubKeys
//      (Preferences root, int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    List<String> results = new ArrayList<String>();
//    int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
//        new Integer(hkey), toCstr(key), new Integer(KEY_READ)
//    });
//    if (handles[1] != REG_SUCCESS) {
//      return null;
//    }
//    int[] info = (int[]) regQueryInfoKey.invoke(root,
//        new Object[]{new Integer(handles[0])});
//
//    int count = info[0]; // Fix: info[2] was being used here with wrong results. Suggested by davenpcj, confirmed by Petrucio
//    int maxlen = info[3]; // value length max
//    for (int index = 0; index < count; index++) {
//      byte[] name = (byte[]) regEnumKeyEx.invoke(root, new Object[]{
//          new Integer
//              (handles[0]), new Integer(index), new Integer(maxlen + 1)
//      });
//      results.add(new String(name).trim());
//    }
//    regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
//    return results;
//  }
//
//  private static int[] createKey(Preferences root, int hkey, String key)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    return (int[]) regCreateKeyEx.invoke(root,
//        new Object[]{new Integer(hkey), toCstr(key)});
//  }
//
//  private static void writeStringValue
//      (Preferences root, int hkey, String key, String valueName, String value)
//      throws IllegalArgumentException, IllegalAccessException,
//      InvocationTargetException {
//    int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
//        new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
//
//    regSetValueEx.invoke(root,
//        new Object[]{
//            new Integer(handles[0]), toCstr(valueName), toCstr(value)
//        });
//    regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
//  }
//
//  // utility
//  private static byte[] toCstr(String str) {
//    byte[] result = new byte[str.length() + 1];
//
//    for (int i = 0; i < str.length(); i++) {
//      result[i] = (byte) str.charAt(i);
//    }
//    result[str.length()] = 0;
//    return result;
//  }
//}
