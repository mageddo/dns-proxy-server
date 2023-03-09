package com.mageddo.jna.os.linux;

import com.mageddo.jna.net.windows.registry.NetworkRegistry;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeProxyCreation;

class NativeImageConfig implements Feature {

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    System.out.println("> NativeImageConfig");
    RuntimeClassInitialization.initializeAtRunTime(Stats.class);
    RuntimeProxyCreation.register(Stats.class);
  }

}
