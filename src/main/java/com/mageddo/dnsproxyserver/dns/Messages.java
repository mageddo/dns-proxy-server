package com.mageddo.dnsproxyserver.dns;

import org.xbill.DNS.Message;

public class Messages {
  public static String simplePrint(Message message){
    return message.toString().substring(0, 10);
  }
}
