package com.mageddo.jna.os.linux;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 * @see https://github.com/sshtools/pty/blob/master/src/main/java/c/stat.java
 */
public class Stat extends Structure {

  /**
   * ID of device containing file
   */
  public NativeLong st_dev;

  /**
   * Inode number
   */
  public NativeLong st_ino;

  /**
   * File type and mode
   */
  public NativeLong st_mode;

  public NativeLong st_nlink;       /* Number of hard links */
  public int st_uid;         /* User ID of owner */
  public int st_gid;         /* Group ID of owner */
  public NativeLong st_rdev;        /* Device ID (if special file) */
  public NativeLong st_size;        /* Total size, in bytes */
  public NativeLong st_blksize;     /* Block size for filesystem I/O */
  public NativeLong st_blocks;      /* Number of 512B blocks allocated */

               /* Since Linux 2.6, the kernel supports nanosecond
                  precision for the following timestamp fields.
                  For the details before Linux 2.6, see NOTES. */

  public Timespec st_atim;  /* Time of last access */
  public Timespec st_mtim;  /* Time of last modification */
  public Timespec st_ctim;  /* Time of last status change */

  /**
   * C type : __syscall_slong_t[3]
   */
  public NativeLong[] __unused = new NativeLong[3];

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList(
        "st_dev", "st_ino", "st_nlink", "st_mode", "st_uid", "st_gid", "st_rdev", "st_size",
        "st_blksize",
        "st_blocks", "st_atim", "st_mtim", "st_ctim", "__unused"
    );
  }

  public static class ByReference extends Stat implements Structure.ByReference {
  }

  public static class ByValue extends Stat implements Structure.ByValue {
  }
}
