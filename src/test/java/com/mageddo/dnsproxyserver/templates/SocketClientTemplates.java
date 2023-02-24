package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.SocketClient;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SocketClientTemplates {
  public static SocketClient buildWith(InputStream in, OutputStream out){
//    return new SocketClient(null, null){
//      @Override
//      public InputStream getIn() {
//        return in;
//      }
//
//      @Override
//      public OutputStream getOut() {
//        return out;
//      }
//    };
    final var client = mock(SocketClient.class);
    doReturn(in).when(client).getIn();
    doReturn(out).when(client).getOut();
    return client;
  }
}
