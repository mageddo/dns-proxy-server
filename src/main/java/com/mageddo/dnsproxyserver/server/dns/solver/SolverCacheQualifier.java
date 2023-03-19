package com.mageddo.dnsproxyserver.server.dns.solver;

import org.apache.commons.lang3.EnumUtils;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SolverCacheQualifier {

  Name name();

  enum Name {
    REMOTE,
    GLOBAL,
    ;

    public static Name fromName(String name) {
      return EnumUtils.getEnumIgnoreCase(Name.class, name);
    }
  }
}
