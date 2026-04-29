package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import dagger.sheath.InjectMock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ConfigFileDAOImplTest {

  @Mock
  ConfigFilePathDAO configFilePathDAO;

  @Spy
  @InjectMocks
  ConfigFileDAOImpl dao;

  @Test
  void mustHaveSureThePathExistsBeforeReturnIt(@TempDir Path tempDir) {


    final var confDir = tempDir.resolve(String.valueOf(UUID.randomUUID()))
        .resolve("conf.json");

    doReturn(confDir)
        .when(this.configFilePathDAO)
        .find()
    ;

    final var path = this.dao.findFilePath();

    assertThat(path.getParent()).exists();

  }

}
