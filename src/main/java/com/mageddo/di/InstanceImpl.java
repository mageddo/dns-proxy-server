package com.mageddo.di;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class InstanceImpl<T> implements Instance<T> {

  private final Collection<T> values;

  public InstanceImpl(T instance){
    this.values = Collections.singleton(instance);
  }

  public InstanceImpl(Collection<T> values) {
    this.values = values;
  }

  @Override
  public Instance select(Annotation... annotations) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isUnsatisfied() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAmbiguous() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void destroy(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Instance select(TypeLiteral typeLiteral, Annotation... annotations) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Instance select(Class aClass, Annotation... annotations) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator iterator() {
    return this.values.iterator();
  }

  @Override
  public T get() {
    throw new UnsupportedOperationException();
  }
}
