package com.mageddo.dnsproxyserver.solver;

import java.util.EnumSet;
import java.util.Set;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.ConfigEntryTypes;

public class SupportedTypes {

  public static final Set<Type> ADDRESSES = ConfigEntryTypes.ADDRESS_SOLVING;

  public static final Set<Type> ADDRESSES_AND_CNAME = EnumSet.of(
      Type.A, Type.AAAA, Type.HTTPS, Type.CNAME
  );
}
