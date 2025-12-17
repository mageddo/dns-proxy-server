package dagger.sheath.templates;

import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;

import dagger.sheath.reflection.Signature;

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
