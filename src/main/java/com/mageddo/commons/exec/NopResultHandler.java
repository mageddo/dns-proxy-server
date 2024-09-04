package com.mageddo.commons.exec;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

public class NopResultHandler implements ExecuteResultHandler {
  @Override
  public void onProcessComplete(int exitValue) {

  }

  @Override
  public void onProcessFailed(ExecuteException e) {

  }
}
