package com.mageddo.wmi;

import com.jacob.com.Variant;
import com.mageddo.commons.lang.ExecutionException;

public class ComUtils {
  public static Variant checkRC(Variant v){
    final var r = v.toInt();
    if(r != 0){
      throw new ExecutionException(r);
    }
    return v;
  }
}
