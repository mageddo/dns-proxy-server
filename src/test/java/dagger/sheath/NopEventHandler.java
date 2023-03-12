package dagger.sheath;

import dagger.sheath.EventHandler;

public class NopEventHandler implements EventHandler<Object> {
  @Override
  public void afterSetup(Object component) {

  }
}
