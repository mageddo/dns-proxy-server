package com.mageddo.dnsproxyserver.di;

import java.util.List;

import com.mageddo.di.Eager;
import com.mageddo.json.JsonUtils;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import lombok.SneakyThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextCompTest {

  Context context = Context.create();

  @Test
  @SneakyThrows
  void mustRegisterAllEagerBeans() {

    final var registeredBeanNames = this.findRegisteredBeanNames();
    final var existingEagerClasses = this.findExistingEagerClasses();

    assertEquals(
        JsonUtils.prettyWriteValueAsString(existingEagerClasses),
        JsonUtils.prettyWriteValueAsString(registeredBeanNames)
    );

  }

  List<String> findExistingEagerClasses() {
    final var reflections = new Reflections("com.mageddo");
    return reflections.get(Scanners.SubTypes.of(Eager.class)
            .asClass())
        .stream()
        .map(Class::getName)
        .sorted()
        .toList();
  }

  private List<String> findRegisteredBeanNames() {
    return this.context
        .eagerBeans()
        .stream()
        .map(Object::getClass)
        .map(Class::getName)
        .sorted()
        .toList();
  }
}
