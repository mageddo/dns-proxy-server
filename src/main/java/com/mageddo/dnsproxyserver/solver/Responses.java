package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dns.utils.Messages;

public class Responses {
  public static boolean hasFlag(Response r, int flag) {
    if (r == null) {
      return false;
    }
    return Messages.hasFlag(r.getMessage(), flag);
  }

  public static boolean isAuthoritative(Response res) {
    return Messages.isAuthoritative(res.getMessage());
  }

  public static boolean isRecursionAvailable(Response res) {
    return Messages.isRecursionAvailable(res.getMessage());
  }

  public static boolean isSuccess(Response res) {
    return Messages.isSuccess(res.getMessage());
  }
}
