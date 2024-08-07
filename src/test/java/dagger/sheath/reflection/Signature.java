package dagger.sheath.reflection;

import com.google.common.reflect.TypeToken;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Value
@Builder
@EqualsAndHashCode(of = "typeArguments")
public class Signature {

  private Class<?> clazz;
  private Type[] typeArguments;

  public static Signature of(Field f) {
    return Signature
      .builder()
      .clazz(f.getType())
      .typeArguments(new Type[]{f.getGenericType()})
      .build();
  }

  public static Signature of(Type type) {
    return Signature
      .builder()
      .clazz(TypeToken.of(type).getRawType())
      .typeArguments(findTypeArguments(type))
      .build();
  }

  private static Type[] findTypeArguments(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments();
    }
    return null;
  }

  public boolean isSameOrInheritFrom(Signature signature) {
    return false;
  }

  public String getFirstTypeArgumentName() {
    if (this.typeArguments != null && this.typeArguments.length > 0) {
      return this.typeArguments[0].getTypeName();
    }
    return null;
  }
}
