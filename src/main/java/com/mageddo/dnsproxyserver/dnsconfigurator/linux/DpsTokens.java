package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

public class DpsTokens {
  public static final String COMMENT_END = "# dps-comment";
  public static final String DPS_ENTRY_COMMENT = "# dps-entry";
  public static final String COMMENT = "#";

  public static String comment(String line) {
    return String.format("# %s # dps-comment", line);
  }

  public static String uncomment(final String line) {
    return line
      .substring(
        2,
        line
          .indexOf(" " + COMMENT_END)
      );
  }
}
