package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.concurrent.Threads;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverCacheFactoryTest {

  SolverCacheFactory factory = spy(new SolverCacheFactory(
    new SolverCache(CacheName.Name.GLOBAL), new SolverCache(CacheName.Name.GLOBAL)
  ));

  @Test
  void mustClearCacheInBackground(){
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
