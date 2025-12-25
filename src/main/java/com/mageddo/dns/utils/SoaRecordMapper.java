package com.mageddo.dns.utils;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TextParseException;

public class SoaRecordMapper {

  public static final int SERIAL = 1;
  public static final int REFRESH = 3600;
  public static final int RETRY = 600;
  public static final int EXPIRE = 86400;
  public static final int MINIMUM = 60;

  public static SOARecord of(Name zone) {
    try {
      final var mname = Name.fromString("ns." + zone);
      final var rname = Name.fromString("dps." + zone);
      return new SOARecord(
          zone,
          DClass.IN,
          256,
          mname,
          rname,
          SERIAL,
          REFRESH,
          RETRY,
          EXPIRE,
          MINIMUM
      );
    } catch (TextParseException e) {
      throw new IllegalArgumentException(e);
    }

  }
}
