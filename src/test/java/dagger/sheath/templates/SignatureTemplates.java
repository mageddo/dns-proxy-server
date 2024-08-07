package dagger.sheath.templates;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.sheath.reflection.Signature;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.graalvm.collections.Pair;

import java.lang.reflect.Method;
import java.util.List;

public class SignatureTemplates {
  public static Signature listOfNumber() {
    return Signature.of(new TypeReference<List<Number>>() {}.getType());
  }

  public static Signature listOfInteger() {
    return Signature.of(new TypeReference<List<Integer>>() {}.getType());
  }

  public static Signature listOfString() {
    return Signature.of(new TypeReference<List<String>>() {}.getType());
  }

  public static Signature list() {
    return Signature.of(new TypeReference<List>() {}.getType());
  }

  public static Signature pairOfString() {
    return Signature.of(new TypeReference<Pair<String, String>>() {}.getType());
  }

  public static Signature pair() {
    return Signature.of(new TypeReference<Pair>() {}.getType());
  }

  public static Signature pairOfStringAndInteger() {
    return Signature.of(new TypeReference<Pair<String, Integer>>() {}.getType());
  }

  public static Method ofMethodIteratorList() {
    return MethodUtils.getMatchingMethod(List.class, "iterator");
  }
}
