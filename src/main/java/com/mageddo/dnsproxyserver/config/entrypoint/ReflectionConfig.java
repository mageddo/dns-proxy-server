package com.mageddo.dnsproxyserver.config.entrypoint;

import nativeimage.Reflection;

@Reflection(
  scanPackage = "com.mageddo.dnsproxyserver.config.entrypoint",
  publicConstructors = true, constructors = true, declaredConstructors = true,
  publicMethods = true, declaredMethods = true,
  publicFields = true, declaredFields = true
)
public class ReflectionConfig {

}
