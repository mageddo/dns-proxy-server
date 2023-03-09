package com.mageddo.jna.net.windows.registry;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

class NativeImageConfig implements Feature {

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    System.out.println("> NativeImageConfig");
    RuntimeClassInitialization.initializeAtRunTime(NetworkRegistry.class);
  }

}
