package com.mageddo.utils.dagger;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.utils.dagger.mockito.EventHandler;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Events implements EventHandler<Context> {
  @Override
  public void afterSetup(Context component) {
    log.info("status=startingDPS");
    component.start();
    RestAssured.port = 5380;
//    Threads.sleep(5000);
  }
}
