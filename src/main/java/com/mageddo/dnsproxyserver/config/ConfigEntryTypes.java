package com.mageddo.dnsproxyserver.config;

import java.util.EnumSet;
import java.util.Set;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;

public class ConfigEntryTypes {

  public static final Set<Type> ADDRESS_SOLVING = EnumSet.of(
      Type.A, Type.AAAA, Type.HTTPS
  );

  public static boolean isAddressSolving(Type type) {
    return ADDRESS_SOLVING.contains(type);
  }
}
