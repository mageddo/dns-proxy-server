package com.mageddo.dnsproxyserver.di;

import dagger.Module;

public interface StartupEvent {
  void onStart();
}
