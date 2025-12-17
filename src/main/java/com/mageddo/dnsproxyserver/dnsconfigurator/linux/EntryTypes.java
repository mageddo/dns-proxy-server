package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.conf.parser.EntryType;

public class EntryTypes {

  public static final String COMMENT = "COMMENT";
  public static final String COMMENTED_SERVER = "COMMENTED_SERVER";
  public static final String SERVER = "SERVER";
  public static final String DPS_SERVER = "DPS_SERVER";
  private static final String SEARCH = "SEARCH";
  public static final String OTHER = "OTHER";

  // parsed types
  public static final EntryType COMMENT_TYPE = EntryType.of(COMMENT);
  public static final EntryType COMMENTED_SERVER_TYPE = EntryType.of(COMMENTED_SERVER);
  public static final EntryType SERVER_TYPE = EntryType.of(SERVER);
  public static final EntryType DPS_SERVER_TYPE = EntryType.of(DPS_SERVER);
  public static final EntryType OTHER_TYPE = EntryType.of(OTHER);
  public static final EntryType SEARCH_TYPE = EntryType.of(SEARCH);
}
