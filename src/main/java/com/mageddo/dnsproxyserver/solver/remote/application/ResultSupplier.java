package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.net.IpAddr;

import java.util.function.Supplier;

public interface ResultSupplier extends Supplier<Result> {
  IpAddr getRemoteAddress();
}
