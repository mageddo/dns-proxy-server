package com.mageddo.dnsproxyserver.solver.docker.entrypoint;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.di.StartupEvents;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DaggerTest(component = Context.class)
class EventListenerCompTest {

  @Inject
  Set<StartupEvent> events;

  @Test
  void mustConfigureNetworkEventListener(){

    // arrange

    // act
    final var found = StartupEvents.exists(this.events, EventListener.class);

    // assert
    assertTrue(found);

  }
}
