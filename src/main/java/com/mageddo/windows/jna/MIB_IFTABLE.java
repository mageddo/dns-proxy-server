package com.mageddo.windows.jna;

import com.mageddo.utils.Fields;
import com.sun.jna.Structure;

import java.util.List;

/**
 * https://learn.microsoft.com/en-us/windows/win32/api/ifmib/ns-ifmib-mib_iftable
 */
public class MIB_IFTABLE extends Structure {

  public static final int DEFAULT_SIZE = 10;

  public MIB_IFTABLE() {
    this(DEFAULT_SIZE);
  }

  public MIB_IFTABLE(int size) {
    this.table = new MIB_IFROW[size];
  }

  public int dwNumEntries;

  public MIB_IFROW table[];

  @Override
  protected List<String> getFieldOrder() {
    return Fields.findNonStaticFieldNames(getClass());
  }

  /**
   * If -1 is specified then will create with default records size which is {@link #DEFAULT_SIZE}
   */
  public static MIB_IFTABLE fromBytesSize(int sizeInBytes) {
    if (sizeInBytes == -1) {
      return new MIB_IFTABLE(DEFAULT_SIZE);
    }
    final int size = sizeInBytes / new MIB_IFROW().size();
    return new MIB_IFTABLE(size);
  }
}
