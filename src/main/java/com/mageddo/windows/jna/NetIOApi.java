package com.mageddo.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface NetIOApi extends StdCallLibrary {

  NetIOApi INSTANCE = Native.loadLibrary("netioapi", NetIOApi.class);

  /**
   * NETIOAPI_API GetInterfaceDnsSettings(
   *   GUID                   Interface,
   *   DNS_INTERFACE_SETTINGS *Settings
   * );
   */
  int GetInterfaceDnsSettings(Guid guid);

}
