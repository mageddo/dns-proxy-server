package com.mageddo.dnsserver.doh;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

import com.mageddo.commons.io.IoUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class DoHServerNetty {

  private static final int PORT = 8444;
  private static final String PATH = "/dns-query";
  private static final byte[] HELLO = "hello world\n".getBytes(CharsetUtil.UTF_8);

  private DoHServerNetty() {
  }

  /**
   * API que você quer: só passar InputStream do .p12 e a senha.
   */
  public static Channel start() throws Exception {

    final var sslCtx = buildSslContext();
    final var boss = new NioEventLoopGroup(1);
    final var worker = new NioEventLoopGroup();

    final var bootstrap = new ServerBootstrap()
      .group(boss, worker)
      .channel(NioServerSocketChannel.class)
      .childOption(ChannelOption.TCP_NODELAY, true)
      .childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(final SocketChannel ch) {
          final var p = ch.pipeline();
          p.addLast(sslCtx.newHandler(ch.alloc()));
          p.addLast(new AlpnNegotiationHandler());
        }
      });

    final var ch = bootstrap.bind(PORT).sync().channel();

    // Fecha loops junto quando o channel for fechado
    ch.closeFuture().addListener((ChannelFutureListener) f -> {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    });

    System.out.println("Listening on https://localhost:" + PORT + PATH + " (HTTP/1.1 + HTTP/2)");
    return ch;
  }

  /**
   * Main só pra facilitar teste local:
   * ./gradlew run --args="/caminho/server.p12 changeit"
   */
  public static void main(final String[] args) throws Exception {
    final var ch = start();
    ch.closeFuture().sync();
  }

  private static SslContext buildSslContext() throws Exception {
    final var in = IoUtils.getResourceAsStream("/META-INF/resources/doh-server.p12");
    return buildSslContext(in, "changeit".toCharArray());
  }

  private static SslContext buildSslContext(final InputStream p12, final char[] password) throws Exception {
    // Carrega PKCS12 do InputStream
    final var ks = KeyStore.getInstance("PKCS12");
    ks.load(p12, password);

    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, password);

    return SslContextBuilder
      .forServer(kmf)
      .sslProvider(SslProvider.JDK) // melhor compatibilidade no native-image
      .applicationProtocolConfig(new ApplicationProtocolConfig(
        ApplicationProtocolConfig.Protocol.ALPN,
        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
        ApplicationProtocolNames.HTTP_2,
        ApplicationProtocolNames.HTTP_1_1
      ))
      .build();
  }

  private static final class AlpnNegotiationHandler extends ApplicationProtocolNegotiationHandler {
    private AlpnNegotiationHandler() {
      super(ApplicationProtocolNames.HTTP_1_1);
    }

    @Override
    protected void configurePipeline(final ChannelHandlerContext ctx, final String protocol) {
      if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
        configureHttp2(ctx);
        return;
      }
      if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
        configureHttp1(ctx);
        return;
      }
      throw new IllegalStateException("Unknown protocol: " + protocol);
    }

    private void configureHttp1(final ChannelHandlerContext ctx) {
      final var p = ctx.pipeline();
      p.addLast(new HttpServerCodec());
      p.addLast(new HttpObjectAggregator(1 << 20));
      p.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
        @Override
        protected void channelRead0(final ChannelHandlerContext c, final FullHttpRequest req) {
          final var uri = req.uri();
          if (!PATH.equals(uri)) {
            send(c, NOT_FOUND, "not found\n");
            return;
          }
          send(c, OK, "hello world\n");
        }

        private void send(final ChannelHandlerContext c, final HttpResponseStatus status, final String body) {
          final var content = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
          final var res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
          res.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
          res.headers().setInt(CONTENT_LENGTH, content.readableBytes());
          res.headers().set(CONNECTION, CLOSE);
          c.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
        }
      });
    }

    private void configureHttp2(final ChannelHandlerContext ctx) {
      final var p = ctx.pipeline();

      final var frameCodec = Http2FrameCodecBuilder.forServer().build();
      final var childHandler = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(final Channel ch) {
          ch.pipeline().addLast(new Http2StreamHandler());
        }
      };

      p.addLast(frameCodec);
      p.addLast(new Http2MultiplexHandler(childHandler));
    }
  }

  private static final class Http2StreamHandler extends SimpleChannelInboundHandler<Http2Frame> {

    private boolean sawHeaders;
    private CharSequence path;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Http2Frame frame) {
      if (frame instanceof Http2HeadersFrame headersFrame) {
        sawHeaders = true;
        final var headers = headersFrame.headers();
        path = headers.path();

        if (headersFrame.isEndStream()) {
          respond(ctx, path);
        }
        return;
      }

      if (frame instanceof Http2DataFrame dataFrame) {
        // aqui ignoramos body e respondemos hello world mesmo
        dataFrame.release();
        if (sawHeaders && dataFrame.isEndStream()) {
          respond(ctx, path);
        }
      }
    }

    private void respond(final ChannelHandlerContext ctx, final CharSequence reqPath) {
      final var ok = reqPath != null && PATH.contentEquals(reqPath);

      final var headers = new DefaultHttp2Headers()
        .status(ok ? "200" : "404")
        .set("content-type", "text/plain; charset=utf-8");

      ctx.write(new DefaultHttp2HeadersFrame(headers, false));
      ctx.writeAndFlush(new DefaultHttp2DataFrame(
        Unpooled.wrappedBuffer(ok ? HELLO : "not found\n".getBytes(CharsetUtil.UTF_8)),
        true
      ));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }
  }
}
