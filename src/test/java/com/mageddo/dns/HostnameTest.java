package com.mageddo.dns;

import org.junit.jupiter.api.Test;

import testing.templates.HostnameTemplates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HostnameTest {

  @Test
  void mustEndsWith() {
    final var hostname = Hostname.of(HostnameTemplates.NGINX_COM_BR);
    assertTrue(hostname.endsWith(".com.br"));
  }

  @Test
  void mustNotEndsWith() {
    final var hostname = Hostname.of(HostnameTemplates.NGINX_COM_BR);
    assertFalse(hostname.endsWith(".com"));
  }
}
