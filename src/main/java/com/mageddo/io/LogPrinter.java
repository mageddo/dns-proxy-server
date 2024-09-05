package com.mageddo.io;

import com.mageddo.concurrent.ThreadsV2;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class LogPrinter {

  public static void printInBackground(InputStream in) {
    final var task = (Runnable) () -> {
      final var bf = new BufferedReader(new InputStreamReader(in));
      while (!ThreadsV2.isInterrupted()) {
        try {
          final var line = bf.readLine();
          if (line == null) {
            log.debug("status=outputEnded");
            break;
          }
          log.debug(">>> {}", line);
        } catch (IOException e) {

        }
      }
    };
    Thread
      .ofVirtual()
      .start(task);
  }

}
