package com.mageddo.di;

import com.mageddo.dnsproxyserver.di.Context;
import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Iterator;

@RequiredArgsConstructor
public class CDIImpl extends CDI<Object> {

  private final Context context;

  @Override
  public BeanManager getBeanManager() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Instance<Object> select(Annotation... qualifiers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <U extends Object> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
    return new InstanceImpl<>(this.context.get(subtype));
  }

  @Override
  public <U extends Object> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
    return new InstanceImpl<>(this.context.get(subtype.getRawType()));
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
  public void destroy(Object instance) {

  }

  @Override
  public Iterator<Object> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Context get() {
    return this.context;
  }
}
