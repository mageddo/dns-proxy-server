package com.mageddo.commons.exec;

import com.mageddo.io.LogPrinter;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Map;

@Value
@Builder(toBuilder = true, builderClassName = "RequestBuilder", buildMethodName = "build0")
public class Request {

  @NonNull
  private final CommandLine commandLine;

  private final Duration timeout;

  private final ExecuteResultHandler handler;
  private Map<String, String> env;

  @NonFinal
  private boolean watchingOutput;

  @Builder.Default
  private final Streams streams = Streams.builder()
    .outAndErr(new ByteArrayOutputStream())
    .build();

  public ExecuteStreamHandler getStreamHandler() {
    return this.streams.toStreamHandler();
  }

  public Request printOutToLogsInBackground() {
    if (this.watchingOutput) {
      throw new IllegalStateException("Already watching output");
    }
    this.watchingOutput = true;
    LogPrinter.printInBackground(this.streams.outAndErr.getPipedIn());
    return this;
  }

  public OutputStream getBestOut() {
    return this.streams.getBestOriginalOutput();
  }

  public long getTimeoutInMillis() {
    if (this.timeout == null) {
      return ExecuteWatchdog.INFINITE_TIMEOUT;
    }
    return this.timeout.toMillis();
  }

  public static class RequestBuilder {

    private boolean printLogsInBackground = false;

    public Request build() {
      final var request = this.build0();
      if (this.printLogsInBackground) {
        request.printOutToLogsInBackground();
      }
      return request;
    }

    public RequestBuilder printLogsInBackground() {
      this.printLogsInBackground = true;
      return this;
    }
  }


  @Value
  @Builder(toBuilder = true, builderClassName = "StreamsBuilder")
  public static class Streams {

    private final PipedStream outAndErr;
    private final OutputStream out;
    private final OutputStream err;
    private final InputStream input;

    public PipedStream getBestOut() {
      if (this.outAndErr != null) {
        return this.outAndErr;
      }
      throw new UnsupportedOperationException();
    }

    public OutputStream getBestOriginalOutput() {
      return this.getBestOut().getOriginalOut();
    }

    public static class StreamsBuilder {
      public Streams.StreamsBuilder outAndErr(OutputStream outAndErr) {
        this.outAndErr = new PipedStream(outAndErr);
        return this;
      }
    }

    public ExecuteStreamHandler toStreamHandler() {
      if (this.outAndErr != null) {
        return new PumpStreamHandler(this.outAndErr);
      }
      return new PumpStreamHandler(this.out, this.err, this.input);
    }
  }
}
