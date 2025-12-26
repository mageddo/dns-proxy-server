package com.mageddo.dnsproxyserver.solver.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import org.apache.commons.lang3.EnumUtils;

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheName {

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
