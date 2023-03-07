package com.mageddo.wmi;

import com.jacob.com.SafeArray;
import com.jacob.com.Variant;

public class SafeArrayUtils {

  private SafeArrayUtils() {
  }

  /**
   * https://sourceforge.net/p/jacob-project/discussion/375946/thread/991feab7/#9bd0
   */
  public static SafeArray fromStringArray(final String ... values) {

    SafeArray v = new SafeArray(Variant.VariantString, 1);
    v.setString(0, values[0]);

    final var vari = new Variant(Variant.VariantArray,true);
    vari.putSafeArrayRef(v);
    return v;
  }
}
