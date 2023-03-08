package com.mageddo.jna.os.linux;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class Timespec extends Structure {

  public long tv_sec;
  public long tv_nsec;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("tv_sec", "tv_nsec");
  }

  public static class ByReference extends Timespec implements Structure.ByReference {
  }

  public static class ByValue extends Timespec implements Structure.ByValue {
  }
}
