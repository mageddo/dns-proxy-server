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

  public static void process(
      final Path confFile,
      final IpAddr addr,
      final boolean overrideNameServers
  ) {

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

  /**
   * overrideNameServers=true:
   * - write dps-entries after header comments
   * - create dps-comments with ALL existing active/commented nameservers (dedup), excluding dps server
   * - preserve any non-nameserver lines (comments/options/search/etc.)
   * - do NOT emit inline "# ... # dps-comment" (tests expect only blocks)
   */
  private static String buildOverrideOutput(
      final String dpsNameserverHost,
      final CleanedContent cleaned
  ) {
    final var nameserversToComment = collectNameserversToComment(dpsNameserverHost, cleaned);

    final var lines = cleaned.originalLines();
    final var insertionIndex = indexAfterHeaderComments(lines);

    final var prefix = new ArrayList<>(lines.subList(0, insertionIndex));
    final var suffix = removeActiveNameserverLines(lines.subList(insertionIndex, lines.size()));

    final var outLines = new ArrayList<String>();

    outLines.addAll(prefix);

    // blocks must come after header comments (systemd-resolved expectation)
    outLines.add(BEGIN_ENTRIES);
    outLines.add(nameserverLine(dpsNameserverHost));
    outLines.add(END_ENTRIES);

    if (!nameserversToComment.isEmpty()) {
      outLines.add("");
      outLines.add(BEGIN_COMMENTS);
      for (final var ns : nameserversToComment) {
        outLines.add(commentedNameserverLine(ns));
      }
      outLines.add(END_COMMENTS);
    }

    // preserve remaining config (options/search/etc.) exactly after the blocks
    final var normalizedSuffix = new ArrayList<>(suffix);
    trimLeadingBlankLines(normalizedSuffix);
    outLines.addAll(normalizedSuffix);

    trimTrailingBlankLines(outLines);
    return joinLines(outLines) + LINE_BREAK;
  }

  private static List<String> removeActiveNameserverLines(final List<String> lines) {
    final var out = new ArrayList<String>();
    for (final var line : lines) {
      if (extractActiveNameserver(line) != null) {
        continue;
      }
      out.add(line);
    }
    return out;
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

    // candidates from inline "# ... # dps-comment" and from previous dps-comments blocks
    nameservers.addAll(cleaned.inlineCommentCandidates());

    for (final var line : cleaned.originalLines()) {
      final var active = extractActiveNameserver(line);
      if (active != null) {
        nameservers.add(active);
        continue;
      }

      final var commented = extractCommentedNameserver(line);
      if (commented != null) {
        nameservers.add(commented);
      }
    }

    nameservers.remove(dpsNameserverHost);
    return new ArrayList<>(nameservers);
  }

  private static int indexAfterHeaderComments(final List<String> lines) {
    int i = 0;
    while (i < lines.size()) {
      final var trimmed = lines.get(i).trim();
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
    while (i < lines.size() && lines.get(i).isBlank()) {
      i++;
    }
    return i;
  }

  private static void ensureBlankLine(final List<String> lines) {
    if (lines.isEmpty()) {
      return;
    }
    if (!lines.getLast().isBlank()) {
      lines.add("");
    }
  }

  // -------------------------------------------------------------------------
  // Remove / Restore DPS artifacts
  // -------------------------------------------------------------------------

  private static CleanedContent removeDpsArtifacts(final String normalizedContent) {
    final var lines = splitLines(normalizedContent);

    final var cleaned = new ArrayList<String>();
    final var commentCandidates = new LinkedHashSet<String>();

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

      // Collect nameservers from previous dps-comments blocks (idempotency fix)
      if (insideCommentsBlock) {
        final var uncommented = uncommentNameserverIfPresent(line); // "# nameserver X" -> "nameserver X"
        final var ns = uncommented == null ? null : extractActiveNameserver(uncommented);
        if (ns != null) {
          commentCandidates.add(ns);
        }
        continue;
      }

      // drop managed entries block completely
      if (insideEntriesBlock) {
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
          commentCandidates.add(ns);
        }
        continue;
      }

      cleaned.add(line);
    }

    trimLeadingBlankLines(cleaned);
    trimTrailingBlankLines(cleaned);

    return new CleanedContent(cleaned, new ArrayList<>(commentCandidates));
  }

  private static String restoreFromContent(final String normalizedContent) {
    final var lines = splitLines(normalizedContent);
    final var restored = new ArrayList<String>();

    boolean insideEntriesBlock = false;
    boolean insideCommentsBlock = false;

    // nameservers read from the dps-comments block; emitted when block ends
    final var nameserversFromBlock = new ArrayList<String>();

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
        nameserversFromBlock.clear();
        continue;
      }
      if (trimmed.equals(END_COMMENTS)) {
        insideCommentsBlock = false;

        // emit restored nameservers exactly where the block was
        final var seen = new LinkedHashSet<String>();
        for (final var ns : nameserversFromBlock) {
          if (seen.add(ns)) {
            restored.add(nameserverLine(ns));
          }
        }
        continue;
      }

      if (insideEntriesBlock) {
        continue;
      }

      if (insideCommentsBlock) {
        final var uncommented = uncommentNameserverIfPresent(line); // "# nameserver X" -> "nameserver X"
        final var ns = uncommented == null ? null : extractActiveNameserver(uncommented);
        if (ns != null) {
          nameserversFromBlock.add(ns);
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

      // IMPORTANT: tests expect blank lines removed on restore
      if (line.isBlank()) {
        continue;
      }

      restored.add(line);
    }

    trimTrailingBlankLines(restored);
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

  private static String extractCommentedNameserver(final String line) {
    final var trimmed = line.trim();
    if (!trimmed.startsWith("#")) {
      return null;
    }
    final var uncommented = trimmed.substring(1).trim(); // remove '#'
    return extractActiveNameserver(uncommented); // "nameserver X"
  }

  private static String uncommentNameserverIfPresent(final String line) {
    var trimmed = line.trim();
    if (!trimmed.startsWith("#")) {
      return null;
    }
    trimmed = trimmed.substring(1).trim();
    return trimmed.startsWith("nameserver") ? trimmed : null;
  }

  private static String restoreInlineDpsComment(final String line) {
    // "# nameserver 8.8.8.8 # dps-comment" -> "nameserver 8.8.8.8"
    final var withoutSuffix = line.replace(DPS_COMMENT_SUFFIX, "").trim();
    var trimmed = withoutSuffix.trim();
    if (trimmed.startsWith("#")) {
      trimmed = trimmed.substring(1).trim();
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
    return s.replace("\r\n", LINE_BREAK).replace("\r", LINE_BREAK);
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
    while (!lines.isEmpty() && lines.getFirst().isBlank()) {
      lines.removeFirst();
    }
  }

  private static void trimTrailingBlankLines(final List<String> lines) {
    while (!lines.isEmpty() && lines.getLast().isBlank()) {
      lines.removeLast();
    }
  }

  private record DnsAddress(String address) {}

  private record CleanedContent(List<String> originalLines, List<String> inlineCommentCandidates) {}
}
