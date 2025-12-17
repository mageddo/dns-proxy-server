package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.converter.Converter;
import com.mageddo.dnsproxyserver.config.dataformat.v3.converter.ConverterFactory;

import lombok.RequiredArgsConstructor;

import static com.mageddo.utils.Files.deleteQuietly;
import static com.mageddo.utils.Files.findExtension;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFileDAOImpl implements ConfigFileDAO {

  private final ConfigFilePathDAO configFilePathDAO;
  private final ConverterFactory converterFactory;

  @Override
  public void save(Config config) {
    try {
      final var path = this.findFilePath();
      final var converter = this.findConverter(path);
      final var raw = converter.to(config);
      Files.writeString(
          path,
          raw,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING
      );
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Config find() {
    try {
      final var path = this.findFilePath();
      final var converter = this.findConverter(path);
      if (!Files.exists(path)) {
        return null;
      }
      final var raw = Files.readString(path, StandardCharsets.UTF_8);
      return converter.of(raw);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void delete() {
    deleteQuietly(this.configFilePathDAO.find());
  }

  private Path findFilePath() {
    return this.configFilePathDAO.find();
  }

  private Converter findConverter(Path path) {
    final var extension = findExtension(path);
    return this.converterFactory.find(extension);
  }
}
