package com.mageddo.utils.dagger.mockito;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DaggerExtension.class)
public @interface DaggerTest {

  Class<?> component();

  String createMethod() default "create";

}
