package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.mageddo.dnsproxyserver.utils.Dns;
import com.mageddo.net.IpAddr;

public class ResolvconfConfiguratorV2 {

  private static final String BEGIN_ENTRIES = "# BEGIN dps-entries";
  private static final String END_ENTRIES = "# END dps-entries";
  private static final String BEGIN_COMMENTS = "# BEGIN dps-comments";
  private static final String END_COMMENTS = "# END dps-comments";

  private static final String DPS_ENTRY_SUFFIX = "# dps-entry";
  private static final String DPS_COMMENT_SUFFIX = "# dps-comment";
  private static final String LINE_BREAK = "\n";

  public static void process(final Path confFile, final IpAddr addr) {
    process(confFile, addr, true);
  }

  public static void process(final Path confFile, final IpAddr addr,
      final boolean overrideNameServers) {

    Dns.validateIsDefaultPort(addr);

    final var dns = parseDnsAddress(addr.getIpAsText());
    final var content = readNormalized(confFile);
    final var cleaned = removeDpsArtifacts(content);

    final var output = overrideNameServers
        ? buildOverrideOutput(dns.address(), cleaned)
        : buildNonOverrideOutput(dns.address(), cleaned);

    writeString(confFile, output);
  }

  public static void restore(final Path confFile) {
    final var content = readNormalized(confFile);
    final var restored = restoreFromContent(content);
    writeString(confFile, restored);
  }

  // -------------------------------------------------------------------------
  // Build outputs
  // -------------------------------------------------------------------------

  private static String buildOverrideOutput(
      final String dpsNameserverHost,
      final CleanedContent cleaned
  ) {
    final var nameserversToComment =
        collectNameserversToComment(dpsNameserverHost, cleaned);

    final var out = new StringBuilder();

    append(out, BEGIN_ENTRIES);
    append(out, nameserverLine(dpsNameserverHost));
    append(out, END_ENTRIES);

    if (nameserversToComment.isEmpty()) {
      return out.toString();
    }

    append(out, "");
    append(out, BEGIN_COMMENTS);

    for (final var ns : nameserversToComment) {
      append(out, commentedNameserverLine(ns));
    }

    append(out, END_COMMENTS);

    return out.toString();
  }

  private static void append(final StringBuilder out, final String value) {
    out.append(value)
        .append(LINE_BREAK);
  }

  private static String buildNonOverrideOutput(
      final String dpsNameserverHost, final CleanedContent cleaned
  ) {

    final var lines = cleaned.originalLines();
    final var insertionIndex = indexAfterHeaderComments(lines);
    final var out = new ArrayList<>(lines.subList(0, insertionIndex));

    ensureBlankLine(out);
    out.add(BEGIN_ENTRIES);
    out.add(nameserverLine(dpsNameserverHost));
    out.add(END_ENTRIES);
    out.add("");

    final var remainderStart = skipBlankLines(lines, insertionIndex);
    out.addAll(lines.subList(remainderStart, lines.size()));

    trimTrailingBlankLines(out);
    return joinLines(out) + LINE_BREAK;
  }

  private static List<String> collectNameserversToComment(
      final String dpsNameserverHost, final CleanedContent cleaned
  ) {
    final var nameservers = new LinkedHashSet<String>();

    // inline "# ... # dps-comment" captured during cleanup
    nameservers.addAll(cleaned.inlineCommentCandidates());

    // remaining active nameservers in the file
    for (final var line : cleaned.originalLines()) {
      final var ns = extractActiveNameserver(line);
      if (ns != null) {
        nameservers.add(ns);
      }
    }

    nameservers.remove(dpsNameserverHost);
    return new ArrayList<>(nameservers);
  }

  private static int indexAfterHeaderComments(final List<String> lines) {
    int i = 0;
    while (i < lines.size()) {
      final var trimmed = lines.get(i)
          .trim();
      if (!trimmed.startsWith("#")) {
        break;
      }
      if (isDpsMarker(trimmed)) {
        break;
      }
      i++;
    }
    return i;
  }

  private static int skipBlankLines(final List<String> lines, final int startIndex) {
    int i = startIndex;
    while (i < lines.size() && lines.get(i)
        .isBlank()) {
      i++;
    }
    return i;
  }

  private static void ensureBlankLine(final List<String> lines) {
    if (lines.isEmpty()) {
      return;
    }
    if (!lines.getLast()
        .isBlank()) {
      lines.add("");
    }
  }

  // -------------------------------------------------------------------------
  // Remove / Restore DPS artifacts
  // -------------------------------------------------------------------------

  private static CleanedContent removeDpsArtifacts(final String normalizedContent) {
    final var lines = splitLines(normalizedContent);

    final var cleaned = new ArrayList<String>();
    final var inlineCommentCandidates = new LinkedHashSet<String>();

    boolean insideEntriesBlock = false;
    boolean insideCommentsBlock = false;

    for (final var line : lines) {
      final var trimmed = line.trim();

      if (trimmed.equals(BEGIN_ENTRIES)) {
        insideEntriesBlock = true;
        continue;
      }
      if (trimmed.equals(END_ENTRIES)) {
        insideEntriesBlock = false;
        continue;
      }
      if (trimmed.equals(BEGIN_COMMENTS)) {
        insideCommentsBlock = true;
        continue;
      }
      if (trimmed.equals(END_COMMENTS)) {
        insideCommentsBlock = false;
        continue;
      }

      // drop managed blocks completely (both entries and comments)
      if (insideEntriesBlock || insideCommentsBlock) {
        continue;
      }

      // drop inline dps-entry
      if (isInlineDpsEntry(line)) {
        continue;
      }

      // capture inline dps-comment and drop the line from output
      if (isInlineDpsComment(line)) {
        final var restored = restoreInlineDpsComment(line);
        final var ns = restored == null ? null : extractActiveNameserver(restored);
        if (ns != null) {
          inlineCommentCandidates.add(ns);
        }
        continue;
      }

      cleaned.add(line);
    }

    trimLeadingBlankLines(cleaned);
    trimTrailingBlankLines(cleaned);

    return new CleanedContent(cleaned, new ArrayList<>(inlineCommentCandidates));
  }

  private static String restoreFromContent(final String normalizedContent) {
    final var lines = splitLines(normalizedContent);
    final var restored = new ArrayList<String>();

    boolean insideEntriesBlock = false;
    boolean insideCommentsBlock = false;

    for (final var line : lines) {
      final var trimmed = line.trim();

      if (trimmed.equals(BEGIN_ENTRIES)) {
        insideEntriesBlock = true;
        continue;
      }
      if (trimmed.equals(END_ENTRIES)) {
        insideEntriesBlock = false;
        continue;
      }
      if (trimmed.equals(BEGIN_COMMENTS)) {
        insideCommentsBlock = true;
        continue;
      }
      if (trimmed.equals(END_COMMENTS)) {
        insideCommentsBlock = false;
        continue;
      }

      if (insideEntriesBlock) {
        continue;
      }

      if (insideCommentsBlock) {
        final var restoredLine = uncommentNameserverIfPresent(line);
        if (restoredLine != null && !restoredLine.isBlank()) {
          restored.add(restoredLine);
        }
        continue;
      }

      if (isInlineDpsEntry(line)) {
        continue;
      }

      if (isInlineDpsComment(line)) {
        final var restoredLine = restoreInlineDpsComment(line);
        if (restoredLine != null && !restoredLine.isBlank()) {
          restored.add(restoredLine);
        }
        continue;
      }

      if (!line.isBlank()) {
        restored.add(line);
      }
    }

    return joinLines(restored) + LINE_BREAK;
  }

  private static DnsAddress parseDnsAddress(final String addr) {
    return new DnsAddress(addr);
  }

  // -------------------------------------------------------------------------
  // Nameserver line parsing
  // -------------------------------------------------------------------------

  private static String extractActiveNameserver(final String line) {
    final var trimmed = line.trim();
    if (!trimmed.startsWith("nameserver")) {
      return null;
    }
    final var parts = trimmed.split("\\s+");
    return parts.length >= 2 ? parts[1].trim() : null;
  }

  private static String uncommentNameserverIfPresent(final String line) {
    var trimmed = line.trim();
    if (!trimmed.startsWith("#")) {
      return null;
    }
    trimmed = trimmed.substring(1)
        .trim();
    return trimmed.startsWith("nameserver") ? trimmed : null;
  }

  private static String restoreInlineDpsComment(final String line) {
    // "# nameserver 8.8.8.8 # dps-comment" -> "nameserver 8.8.8.8"
    final var withoutSuffix = line.replace(DPS_COMMENT_SUFFIX, "")
        .trim();
    var trimmed = withoutSuffix.trim();
    if (trimmed.startsWith("#")) {
      trimmed = trimmed.substring(1)
          .trim();
    }
    return trimmed;
  }

  private static String nameserverLine(final String host) {
    return "nameserver " + host;
  }

  private static String commentedNameserverLine(final String host) {
    return "# " + nameserverLine(host);
  }

  // -------------------------------------------------------------------------
  // DPS markers / inline markers
  // -------------------------------------------------------------------------

  private static boolean isDpsMarker(final String trimmedLine) {
    return trimmedLine.equals(BEGIN_ENTRIES)
        || trimmedLine.equals(END_ENTRIES)
        || trimmedLine.equals(BEGIN_COMMENTS)
        || trimmedLine.equals(END_COMMENTS);
  }

  private static boolean isInlineDpsEntry(final String line) {
    return line.contains(DPS_ENTRY_SUFFIX);
  }

  private static boolean isInlineDpsComment(final String line) {
    return line.contains(DPS_COMMENT_SUFFIX);
  }

  // -------------------------------------------------------------------------
  // IO / text utils
  // -------------------------------------------------------------------------

  private static String readNormalized(final Path path) {
    return normalizeNewlines(readFileOrEmpty(path));
  }

  private static String readFileOrEmpty(final Path path) {
    try {
      if (!Files.exists(path)) {
        return "";
      }
      return Files.readString(path);
    } catch (final IOException e) {
      throw new UncheckedIOException("Failed to read file: " + path, e);
    }
  }

  private static void writeString(final Path path, final String content) {
    try {
      Files.writeString(path, content);
    } catch (final IOException e) {
      throw new UncheckedIOException("Failed to write file: " + path, e);
    }
  }

  private static String normalizeNewlines(final String s) {
    return s.replace("\r\n", LINE_BREAK)
        .replace("\r", LINE_BREAK);
  }

  private static List<String> splitLines(final String normalizedContent) {
    if (normalizedContent.isEmpty()) {
      return List.of();
    }
    return List.of(normalizedContent.split(LINE_BREAK, -1));
  }

  private static String joinLines(final List<String> lines) {
    if (lines.isEmpty()) {
      return "";
    }
    final var out = new StringBuilder();
    for (int i = 0; i < lines.size(); i++) {
      out.append(lines.get(i));
      if (i + 1 < lines.size()) {
        out.append(LINE_BREAK);
      }
    }
    return out.toString();
  }

  private static void trimLeadingBlankLines(final List<String> lines) {
    while (!lines.isEmpty() && lines.getFirst()
        .isBlank()) {
      lines.removeFirst();
    }
  }

  private static void trimTrailingBlankLines(final List<String> lines) {
    while (!lines.isEmpty() && lines.getLast()
        .isBlank()) {
      lines.removeLast();
    }
  }

  private record DnsAddress(String address) {}

  private record CleanedContent(List<String> originalLines, List<String> inlineCommentCandidates) {}
}
