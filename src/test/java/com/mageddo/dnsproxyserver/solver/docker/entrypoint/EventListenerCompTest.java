package com.mageddo.dnsproxyserver.solver.docker.entrypoint;

import ch.qos.logback.classic.Level;
import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.di.StartupEvents;
import com.mageddo.logback.LogbackUtils;
import dagger.sheath.junit.DaggerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DaggerTest(component = Context.class)
class EventListenerCompTest {

  @Inject
  Set<StartupEvent> events;

  @BeforeAll
  static void beforeAll(){
    LogbackUtils.changeLogLevel("com.mageddo", Level.TRACE);
  }

  @Test
  void mustConfigureNetworkEventListener(){

    // arrange
    log.debug("events={}", events);

    // act
    final var found = StartupEvents.exists(this.events, EventListener.class);

    // assert
    assertTrue(found);

  }
}
