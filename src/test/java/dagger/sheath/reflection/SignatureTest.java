package dagger.sheath.reflection;

import dagger.sheath.templates.SignatureTemplates;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureTest {

  @Test
  void mustGetCorrectTypeFromField(){
    final var field = FieldUtils.getField(Car.class, "passengers", true);
    final var signature = Signature.of(field);

    assertEquals(List.class, signature.getClazz());
    assertEquals("java.util.List<java.lang.String>", signature.getFirstTypeArgumentName());

  }

  @Test
  void mustMatchFieldsWithSameTypeAndGenerics(){

    final var passengers = fieldToSignature(Car.class, "passengers");
    final var accessories = fieldToSignature(Car.class, "accessories");

    assertEquals(passengers, accessories);

  }

  @Test
  void fieldsWithDifferentGenericCantMatch(){

    final var passengers = fieldToSignature(Car.class, "passengers");
    final var accessories = fieldToSignature(Car.class, "tripsKms");

    assertNotEquals(passengers, accessories);

  }

  @Test
  void mustMatchFieldsWithCompatibleTypes(){

    final var ancestor = SignatureTemplates.listOfNumber();
    final var impl = SignatureTemplates.listOfInteger();

    assertTrue(ancestor.isSameOrInheritFrom(impl));

  }

  private static Signature fieldToSignature(final Class<Car> clazz, final String fieldName) {
    return Signature.of(FieldUtils.getField(clazz, fieldName, true));
  }

  static class Car {
    List<String> passengers = new ArrayList<>();
    List<String> accessories = new ArrayList<>();
    List<Integer> tripsKms = new ArrayList<>();
  }
}
