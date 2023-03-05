package com.mageddo.windows.jna;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @see https://learn.microsoft.com/en-us/windows/win32/api/iphlpapi/
 * @see https://stackoverflow.com/questions/17876758/jna-getiftable-error
 */
interface IPHelperApi extends StdCallLibrary {

  IPHelperApi INSTANCE = Native.loadLibrary("IpHlpAPI", IPHelperApi.class);

  /**
   * IPHLPAPI_DLL_LINKAGE DWORD GetIfTable(
   * [out]     PMIB_IFTABLE pIfTable,
   * [in, out] PULONG       pdwSize,
   * [in]      BOOL         bOrder
   * );
   *
   * @param pIfTable out - A pointer to a buffer that receives the interface table as a MIB_IFTABLE structure.
   * @param pdwSize  [in, out] - On input, specifies the size in bytes of the buffer pointed to by the pIfTable parameter.
   *                 On output, if the buffer is not large enough to hold the returned interface table,
   *                 the function sets this parameter equal to the required buffer size in bytes.
   * @param bOrder   - A Boolean value that specifies whether the returned interface table should
   *                 be sorted in ascending order by interface index. If this parameter is TRUE, the table is sorted.
   * @return If the function succeeds, the return value is NO_ERROR.
   * <p>
   * If the function fails, the return value is one of the following error codes.
   * @see https://learn.microsoft.com/en-us/windows/win32/api/iphlpapi/nf-iphlpapi-getiftable?redirectedfrom=MSDN
   */
  int GetIfTable(MIB_IFTABLE pIfTable, IntByReference pdwSize, boolean bOrder);

}
