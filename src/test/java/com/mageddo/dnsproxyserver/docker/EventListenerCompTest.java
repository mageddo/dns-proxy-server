package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(component = Context.class)
class EventListenerCompTest {

  @Inject
  Set<StartupEvent> events;

  @Test
  void mustConfigureNetworkEventListener(){

    // arrange

    // act
    final var size = this.events
        .stream()
        .filter(it -> Objects.equals(EventListener.class, it.getClass()))
        .count();

    // assert
    assertEquals(1, size);

  }
}
