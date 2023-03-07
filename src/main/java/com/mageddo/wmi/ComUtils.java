package com.mageddo.wmi;

import com.jacob.com.Variant;
import org.apache.commons.lang3.Validate;

public class ComUtils {
  public static Variant checkRC(Variant v){
    final var r = v.toInt();
    Validate.isTrue(r == 0, "Execution failed, int=%d, hex=%x", r, r);
    return v;
  }
}
