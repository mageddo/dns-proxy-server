package com.mageddo.windows.jna;

import com.mageddo.utils.Fields;
import com.sun.jna.Structure;

import java.util.List;

/**
 * typedef struct _DNS_INTERFACE_SETTINGS {
 * int   Version;
 * int64 Flags;
 * String   Domain;
 * String   NameServer;
 * String   SearchList;
 * int   RegistrationEnabled;
 * int   RegisterAdapterName;
 * int   EnableLLMNR;
 * int   QueryAdapterName;
 * String   ProfileNameServer;
 * } DNS_INTERFACE_SETTINGS;
 * <p>
 * https://learn.microsoft.com/en-us/windows/win32/api/netioapi/ns-netioapi-dns_interface_settings
 * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types
 */
public class DnsSettings extends Structure {

  int Version;
  long Flags;
  String Domain;
  String NameServer;
  String SearchList;
  int RegistrationEnabled;
  int RegisterAdapterName;
  int EnableLLMNR;
  int QueryAdapterName;
  String ProfileNameServer;

  @Override
  protected List<String> getFieldOrder() {
    return Fields.findNonStaticFieldNames(getClass());
  }
}
