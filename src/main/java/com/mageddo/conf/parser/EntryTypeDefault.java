package com.mageddo.conf.parser;

public class EntryTypeDefault implements EntryType {

  private final String name;

  public EntryTypeDefault(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return this.name;
  }
}
