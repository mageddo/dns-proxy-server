package com.mageddo.dnsproxyserver.solver.remote;

import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.net.IpAddr;
import com.mageddo.net.IpAddrs;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import java.net.InetSocketAddress;

@Value
@Builder
public class Request {

  @NonNull
  Message query;

  @NonNull
  Resolver resolver;

  @NonNull
  Integer resolverIndex;

  @NonNull
  StopWatch stopWatch;

  public IpAddr getResolverAddr() {
    return IpAddrs.from(this.getResolverAddress());
  }

  public InetSocketAddress getResolverAddress() {
    return this.getResolver().getAddress();
  }

  public void splitStopWatch() {
    this.stopWatch.split();
  }

  public long getElapsedTimeInMs() {
    return this.stopWatch.getTime() - this.stopWatch.getSplitTime();
  }

  public long getTime() {
    return this.stopWatch.getTime();
  }
}
