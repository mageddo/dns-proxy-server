package dagger.sheath.templates;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.sheath.reflection.Signature;

import java.util.List;

public class SignatureTemplates {
  public static Signature listOfNumber() {
    return Signature.of(new TypeReference<List<Number>>() {}.getType());
  }

  public static Signature listOfInteger() {
    return Signature.of(new TypeReference<List<Integer>>() {}.getType());
  }
}
