package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;

public interface Parser {
  IP parse(String addressExpression);
}
