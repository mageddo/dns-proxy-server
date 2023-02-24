package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.SocketClient;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Mockito.mock;

public class SocketClientTemplates {
  public static SocketClient buildWith(InputStream in, OutputStream out) {
    return new SocketClient(mock(Socket.class), null) {
      private boolean closed;

      @Override
      public InputStream getIn() {
        return in;
      }

      @Override
      public OutputStream getOut() {
        return out;
      }

      @Override
      public void close() throws Exception {
        this.closed = true;
        in.close();
        out.close();
      }

      @Override
      public boolean isOpen() {
        return !this.closed;
      }
    };
  }
}
