package com.mageddo.dnsproxyserver.solver.cache;

import com.mageddo.commons.concurrent.Threads;

import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverCacheFactoryTest {

  SolverCacheFactory factory = spy(new SolverCacheFactory(
      new SolverCache(Name.GLOBAL), new SolverCache(Name.GLOBAL)
  ));

  @Test
  void mustClearCacheInBackground() {
    // arrange
    assertEquals(0, this.factory.getProcessedInBackground());

    // act
    this.factory.scheduleCacheClear();
    Threads.sleep(30);

    // assert
    verify(this.factory).clearCaches();
    assertEquals(1, this.factory.getProcessedInBackground());

  }
}
