package com.mageddo.conf.parser;

public interface EntryType {

  String name();

  static EntryType of(String name) {
    return new EntryTypeDefault(name);
  }
}
