package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.templates.IpAddrTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class DnsConfiguratorsTest {

  @InjectMocks
  @Spy
  DnsConfigurators configurators;

  @Test
  void mustFailAndStopAfterMaxTries(){
    // arrange
    doNothing().when(this.configurators).configureShutdownHook(any());
    doReturn(2).when(this.configurators).getInitialDelay();
    doReturn(5).when(this.configurators).getDelay();
    doReturn(IpAddrTemplates.local()).when(this.configurators).findIpAddr();

    doThrow(new IllegalAccessError("Mocked error")).when(this.configurators).configure(any());

    // act
    this.configurators.onStart();

    // assert
    Threads.sleep(300);
    assertEquals(3, this.configurators.getFailures());
  }

}
