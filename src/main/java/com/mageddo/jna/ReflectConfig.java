package com.mageddo.jna;

import nativeimage.Reflection;

@Reflection(
  scanPackage = "com.mageddo.jna",
  publicConstructors = true, declaredConstructors = true, declaredFields = true, publicFields = true,
  declaredMethods = true, publicMethods = true
)
public class ReflectConfig {
}
