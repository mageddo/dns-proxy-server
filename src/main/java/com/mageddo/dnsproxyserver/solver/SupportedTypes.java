package com.mageddo.dnsproxyserver.solver;

import java.util.EnumSet;
import java.util.Set;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;

public class SupportedTypes {

  public static final Set<Type> ADDRESSES = EnumSet.of(
      Type.A, Type.AAAA, Type.HTTPS
  );

  public static final Set<Type> ADDRESSES_AND_CNAME = EnumSet.of(
      Type.A, Type.CNAME, Type.AAAA, Type.HTTPS
  );
}
