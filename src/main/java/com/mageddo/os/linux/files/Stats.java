package com.mageddo.os.linux.files;

import com.mageddo.os.linux.struct.Stat;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * @see https://man7.org/linux/man-pages/man2/lstat.2.html
 */
public interface Stats extends Library {

  Stats INSTANCE = Native.loadLibrary((Platform.isLinux() ? Platform.C_LIBRARY_NAME : null), Stats.class);

  /**
   * int stat(const char *restrict pathname, struct stat *restrict statbuf);
   */
  int stat(String pathname, Stat.ByReference statbuf);



}