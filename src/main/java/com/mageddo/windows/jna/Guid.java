package com.mageddo.windows.jna;

import com.mageddo.utils.Fields;
import com.sun.jna.Structure;

import java.util.List;
import java.util.UUID;

/**
 * typedef struct _GUID {
 * unsigned long  Data1;
 * unsigned short Data2;
 * unsigned short Data3;
 * unsigned char  Data4[8];
 * } GUID;
 *
 * @see https://learn.microsoft.com/en-us/windows/win32/api/guiddef/ns-guiddef-guid
 */
public class Guid extends Structure {

  int Data1;
  short Data2;
  short Data3;
  char[] Data4 = new char[8];

  @Override
  public String toString() {
    return String.format("%d-%d-%d-%d", this.Data1, this.Data2, this.Data3, this.Data4);
  }

  @Override
  protected List<String> getFieldOrder() {
    return Fields.findNonStaticFieldNames(getClass());
  }

  public static Guid fromString(String guidText) {
    validateIsGuid(guidText);
    final var sections = guidText.split("-");
    final var guid = new Guid();
    guid.Data1 = Integer.parseInt(sections[0], 16);
    guid.Data2 = Byte.parseByte(sections[1], 16);
    guid.Data3 = Byte.parseByte(sections[2], 16);
    guid.Data4 = sections[3].toCharArray();
    return guid;
  }

  private static void validateIsGuid(String guidText) {
    UUID.fromString(guidText);
  }
}
