package dagger.sheath.reflection;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Value
@Builder
@EqualsAndHashCode(of = "genericType")
public class Signature {

  private Class<?> clazz;
  private Type genericType;

  public static Signature of(Field f) {
    return Signature
      .builder()
      .clazz(f.getType())
      .genericType(f.getGenericType())
      .build();
  }
}
