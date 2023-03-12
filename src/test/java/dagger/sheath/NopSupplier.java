package dagger.sheath;

import java.util.function.Supplier;

public class NopSupplier implements Supplier<Object> {
  @Override
  public Object get() {
    throw new UnsupportedOperationException();
  }
}
