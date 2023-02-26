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
import java.util.stream.Collectors;

import static com.mageddo.utils.Files.copyContent;


public class ConfParser {

  public static List<Entry> parse(String in, Function<String, EntryType> parser) {
    return parse(new BufferedReader(new StringReader(in)), parser);
  }

  public static List<Entry> parse(BufferedReader r, Function<String, EntryType> parser) {
    try {
      final var entries = new ArrayList<Entry>();
      String line;
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
    try {
      final var tmpFile = Files.createTempFile("dps", ".conf");
      try (
        var reader = Files.newBufferedReader(source);
        var writer = Files.newBufferedWriter(tmpFile)
      ) {
        writeToOut(reader, writer, parser, t);
      }
      copyContent(tmpFile, target);
      Files.delete(tmpFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void writeToOut(
    BufferedReader reader, BufferedWriter writer,
    Function<String, EntryType> parser, Transformer t
  ) {
    final var lines = parse(reader, parser);
    lines
      .stream()
      .map(t::handle)
      .filter(Objects::nonNull)
      .forEach(line -> writeLine(writer, line));
    final var foundTokens = lines
      .stream()
      .map(it -> it.getType().name())
      .collect(Collectors.toSet());
    writeLine(writer, t.after(!lines.isEmpty(), foundTokens));
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
