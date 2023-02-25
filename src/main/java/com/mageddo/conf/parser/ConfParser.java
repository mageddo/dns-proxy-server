package com.mageddo.conf.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public class ConfParser {

  public static List<Entry> parse(String in, Function<String, EntryType> parser) {
    return parse(new BufferedReader(new StringReader(in)), parser);
  }

  public static List<Entry> parse(BufferedReader r, Function<String, EntryType> parser) {
    try {
      final var entries = new ArrayList<Entry>();
      String line = null;
      while ((line = r.readLine()) != null) {
        entries.add(Entry
          .builder()
          .type(parser.apply(line))
          .line(line)
          .build()
        );
      }
      return entries;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void process(Path conf, Function<String, EntryType> parser, Transformer h) {
    process(conf, conf, parser, h);
  }

  public static void process(Path source, Path target, Function<String, EntryType> parser, Transformer t) {
    try (var reader = Files.newBufferedReader(source); var writer = Files.newBufferedWriter(target)) {
      writeToOut(parser, t, reader, writer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void writeToOut(
    Function<String, EntryType> parser,
    Transformer t,
    BufferedReader reader, BufferedWriter writer
  ) {
    final var lines = parse(reader, parser);
    lines
      .stream()
      .map(t::handle)
      .filter(Objects::nonNull)
      .forEach(line -> writeLine(writer, line));
    writeLine(writer, t.after(!lines.isEmpty()));
  }

  static void writeLine(BufferedWriter writer, String line) {
    try {
      if (line != null) {
        writer.write(line);
        writer.write('\n');
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
