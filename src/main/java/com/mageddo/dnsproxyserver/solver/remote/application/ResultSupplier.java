package com.mageddo.dnsproxyserver.solver.remote.application;

import java.util.function.Supplier;

import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.net.IpAddr;

public interface ResultSupplier extends Supplier<Result> {
  IpAddr getRemoteAddress();
}
