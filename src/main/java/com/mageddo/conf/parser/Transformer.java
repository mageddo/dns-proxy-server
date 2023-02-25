package com.mageddo.conf.parser;

public interface Transformer {

  String handle(Entry entry);

  String after(boolean fileHasContent);
}
