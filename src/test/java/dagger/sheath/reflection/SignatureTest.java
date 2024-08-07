package dagger.sheath.reflection;

import dagger.sheath.templates.SignatureTemplates;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureTest {

  @Test
  void mustConvertFieldToSignature(){
    final var sig = fieldToSignature(Car.class, "passengers");
    assertEquals("Signature(clazz=interface java.util.List, typeArguments=[class java.lang.String])", sig.toString());
  }

  @Test
  void mustGetCorrectTypeFromField(){
    final var signature = fieldToSignature(Car.class, "passengers");
    assertEquals(List.class, signature.getClazz());
    assertEquals("java.lang.String", signature.getFirstTypeArgumentName());

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


  @Test
  void differentNumberOfTypeArgumentsMustNotMatch(){

    final var ancestor = SignatureTemplates.pairOfString();
    final var impl = SignatureTemplates.listOfString();

    assertFalse(ancestor.areTypeArgumentsSameOrInheritFrom(impl));

  }

  @Test
  void fieldsWithIncompatibleTypesArgumentsMustNotMatch(){

    final var ancestor = SignatureTemplates.listOfNumber();
    final var impl = SignatureTemplates.listOfString();

    assertFalse(ancestor.isSameOrInheritFrom(impl));

  }

  @Test
  void fieldsWithMoreThanOneTypeArgumentAndIncompatibleTypesArgumentsMustNotMatch(){

    final var ancestor = SignatureTemplates.pairOfString();
    final var impl = SignatureTemplates.pairOfStringAndInteger();

    assertFalse(ancestor.isSameOrInheritFrom(impl));

  }

  @Test
  void whenImplHasNotTypeArgumentsTheyCantMatch(){

    final var ancestor = SignatureTemplates.pairOfString();
    final var impl = SignatureTemplates.pair();

    assertFalse(ancestor.isSameOrInheritFrom(impl));

  }

  @Test
  void ancestorsWithoutTypeArgumentsSpecificationMustMatch(){

    final var ancestor = SignatureTemplates.list();
    final var impl = SignatureTemplates.listOfString();

    assertTrue(ancestor.isSameOrInheritFrom(impl));

  }

  @Test
  void mustParseMethodReturnTypeWithTypeArguments(){
    final var method = SignatureTemplates.ofMethodIteratorList();

    final var sig = Signature.ofMethodReturnType(method);

    assertEquals("Signature(clazz=interface java.util.Iterator, typeArguments=[E])", sig.toString());
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
