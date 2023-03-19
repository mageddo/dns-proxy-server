package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.xbill.DNS.Message;

import java.time.Duration;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Response {

  /**
   * The effective response.
   */
  @NonNull
  private Message message;

  /**
   * the calculated ttl, can be the specified on the message or calculated to a different one.
   */
  @NonNull
  private Duration ttl;

  @NonNull
  private LocalDateTime createdAt;

  public static Response of(Message message){
    return Response
      .builder()
      .message(message)
      .ttl(Messages.DEFAULT_TTL_DURATION)
      .createdAt(LocalDateTime.now())
      .build();
  }

  public Response withMessage(Message msg) {
    return this
      .toBuilder()
      .message(msg)
      .build()
      ;
  }
}
