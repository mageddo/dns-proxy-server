package dagger.sheath.reflection;

import com.google.common.reflect.TypeToken;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Value
@Builder
@EqualsAndHashCode(of = "typeArguments")
public class Signature {

  private Class<?> clazz;
  private Type[] typeArguments;


  public boolean isSameOrInheritFrom(Signature sig) {
    return this.clazz.isAssignableFrom(sig.getClazz()) && this.areTypeArgumentsSameOrInheritFrom(sig);
  }

  boolean areTypeArgumentsSameOrInheritFrom(Signature sig) {
    if (ObjectUtils.allNull(this.typeArguments, sig.typeArguments)) {
      return true;
    }
    if (this.typeArguments == null && sig.typeArguments != null) {
      return true;
    }
    if (sig.typeArguments == null) {
      return false;
    }
    if (this.typeArguments.length != sig.typeArguments.length) {
      return false;
    }
    for (int i = 0; i < this.typeArguments.length; i++) {
      if (!this.isTypeArgumentSameOrInheritFrom(sig, i)) {
        return false;
      }
    }
    return true;
  }

  private boolean isTypeArgumentSameOrInheritFrom(Signature sig, int i) {
    final var type = TypeToken.of(this.typeArguments[i]).getRawType();
    final var otherType = TypeToken.of(sig.typeArguments[i]).getRawType();
    return type.isAssignableFrom(otherType);
  }

  public String getFirstTypeArgumentName() {
    if (this.typeArguments != null && this.typeArguments.length > 0) {
      return this.typeArguments[0].getTypeName();
    }
    return null;
  }

  public static Signature of(Field f) {
    return Signature
      .builder()
      .clazz(f.getType())
      .typeArguments(findTypeArguments(f.getGenericType()))
      .build();
  }

  public static Signature of(Type type) {
    return Signature
      .builder()
      .clazz(TypeToken.of(type).getRawType())
      .typeArguments(findTypeArguments(type))
      .build();
  }

  public static Signature ofMethodReturnType(Method m) {
    return of(m.getGenericReturnType());
  }

  private static Type[] findTypeArguments(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments();
    }
    return null;
  }


}
