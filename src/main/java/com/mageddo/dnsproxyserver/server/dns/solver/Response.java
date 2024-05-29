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
   * These magic numbers were defined as explained at #370.
   * <br/>
   * "I read it is a common approach to DNS Servers to cache names for more time than specified on the TTL
   * In general people expects the server name address to be updated in some hours, then if DPS caches found
   * hostnames for at least 5 minutes and the don't found for 1 hour, it might speed up a lot."
   */
  public static final Duration DEFAULT_SUCCESS_TTL = Duration.ofMinutes(5);
  public static final Duration DEFAULT_NXDOMAIN_TTL = Duration.ofMinutes(60);

  /**
   * The effective response for the client.
   */
  @NonNull
  private Message message;

  /**
   * The calculated TTL which will be used by the system to cache entries on DPS,
   * it's not the same specified at {@link #message}. It is calculated with default values based on the scenario,
   * see {@link #nxDomain(Message)} or {@link #success(Message)} for examples.
   */
  @NonNull
  private Duration ttl;

  @NonNull
  private LocalDateTime createdAt;

  public static Response of(Message message, Duration ttl) {
    return Response
      .builder()
      .message(message)
      .ttl(ttl)
      .createdAt(LocalDateTime.now())
      .build();
  }

  public static Response nxDomain(Message message) {
    return of(message, DEFAULT_NXDOMAIN_TTL);
  }

  public static Response success(Message message) {
    return of(message, DEFAULT_SUCCESS_TTL);
  }

  public static Response internalSuccess(Message message) {
    return of(message, Messages.DEFAULT_TTL_DURATION);
  }

  public Response withMessage(Message msg) {
    return this
      .toBuilder()
      .message(msg)
      .build()
      ;
  }

  public Response withTTL(Duration ttl) {
    return this.toBuilder()
      .ttl(ttl)
      .build();
  }
}
