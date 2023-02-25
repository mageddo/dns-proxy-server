package com.mageddo.dnsproxyserver.dnsconfigurator.linux.systemdresolved;

import com.mageddo.conf.parser.EntryType;

public class EntryTypes {

  public static final String COMMENT = "COMMENT";

  public static final String COMMENTED_SERVER = "COMMENTED_SERVER";

  public static final String SERVER = "SERVER";

  public static final String DPS_SERVER = "DPS_SERVER";

  public static final String OTHER = "OTHER";


  public static final EntryType COMMENT_TYPE = EntryType.of(COMMENT);

  public static final EntryType COMMENTED_SERVER_TYPE = EntryType.of(COMMENTED_SERVER);

  public static final EntryType SERVER_TYPE = EntryType.of(SERVER);

  public static final EntryType DPS_SERVER_TYPE = EntryType.of(DPS_SERVER);

  public static final EntryType OTHER_TYPE = EntryType.of(OTHER);
}
