package com.mageddo.dnsserver.doh;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.KeyManagerFactory;

import com.mageddo.commons.io.IoUtils;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsserver.RequestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
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
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
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

import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE;

public final class DoHServerNetty {

  public static final String DOH_PATH = "/dns-query";
  public static final String DOH_MEDIA_TYPE = "application/dns-message";

  private DoHServerNetty() {
  }

  public static void nativeImageFixes() {
    // Evita caminhos que dependem de Unsafe/JCTools no native
    System.setProperty("io.netty.noUnsafe", "true");
    System.setProperty("io.netty.allocator.type", "unpooled");

    // Opcional: reduz chance de alocação direct em ambientes mais chatos
    System.setProperty("io.netty.noPreferDirect", "true");

    // Opcional: desliga recycler (menos otimização, mais previsibilidade)
    System.setProperty("io.netty.recycler.maxCapacity", "0");

  }

  /**
   * Hook principal: você recebe a query DNS (wire bytes) e devolve o response DNS (wire bytes).
   * <p>
   * IMPORTANTE: DoH é 1 mensagem DNS por request.
   */
  @FunctionalInterface
  public interface DnsMessageHandler {
    byte[] handle(byte[] dnsQueryBytes) throws Exception;
  }

  // ---------------------------------------------------------------------------
  // Server (separado dos handlers)
  // ---------------------------------------------------------------------------
  public static final class Server {

    private final InputStream p12;
    private final char[] password;
    private final DnsMessageHandler dnsHandler;
    private final InetSocketAddress address;

    public Server(
        final InetSocketAddress address,
        final InputStream p12,
        final char[] password,
        final DnsMessageHandler dnsHandler
    ) {
      this.address = address;
      this.p12 = p12;
      this.password = password;
      this.dnsHandler = dnsHandler;
    }

    public Channel start() throws Exception {
      final var sslCtx = buildSslContext(this.p12, this.password);

      final var boss = new NioEventLoopGroup(1);
      final var worker = new NioEventLoopGroup();

      final var bootstrap = new ServerBootstrap()
          .group(boss, worker)
          .channel(NioServerSocketChannel.class)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) {
              final var p = ch.pipeline();
              p.addLast(sslCtx.newHandler(ch.alloc()));
              p.addLast(new AlpnNegotiationHandler(dnsHandler));
            }
          });

      final var ch = bootstrap.bind(this.address)
          .sync()
          .channel();

      ch.closeFuture()
          .addListener((ChannelFutureListener) f -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
          });

      return ch;
    }
  }

  // ---------------------------------------------------------------------------
  // HTTP Route Handlers (separado do Server)
  // ---------------------------------------------------------------------------
  static final class HttpRouteHandlers {

    private final DnsMessageHandler dnsHandler;

    HttpRouteHandlers(final DnsMessageHandler dnsHandler) {
      this.dnsHandler = dnsHandler;
    }

    /**
     * HTTP/1.1 router
     */
    void handleHttp1(final ChannelHandlerContext ctx, final FullHttpRequest req) {
      try {
        final var method = req.method();
        final var uri = req.uri();

        if (!uri.startsWith(DOH_PATH)) {
          sendHttp1Text(ctx, NOT_FOUND, "not found\n");
          return;
        }

        if (HttpMethod.GET.equals(method)) {
          final var dnsQueryBytes = DohCodec.parseGetDnsParam(uri);
          if (dnsQueryBytes == null) {
            sendHttp1Text(ctx, BAD_REQUEST, "missing or invalid dns param\n");
            return;
          }
          final var dnsResponseBytes = dnsHandler.handle(dnsQueryBytes);
          sendHttp1Dns(ctx, OK, dnsResponseBytes);
          return;
        }

        if (HttpMethod.POST.equals(method)) {
          final var contentType = headerLower(req.headers(), CONTENT_TYPE);
          if (contentType == null || !contentType.startsWith(DOH_MEDIA_TYPE)) {
            sendHttp1Text(ctx, UNSUPPORTED_MEDIA_TYPE,
                "content-type must be application/dns-message\n"
            );
            return;
          }

          final var dnsQueryBytes = ByteBufs.toByteArray(req.content());
          if (dnsQueryBytes.length == 0) {
            sendHttp1Text(ctx, BAD_REQUEST, "empty body\n");
            return;
          }

          final var dnsResponseBytes = dnsHandler.handle(dnsQueryBytes);
          sendHttp1Dns(ctx, OK, dnsResponseBytes);
          return;
        }

        sendHttp1Text(ctx, METHOD_NOT_ALLOWED, "method not allowed\n");
      } catch (final Exception e) {
        sendHttp1Text(ctx, INTERNAL_SERVER_ERROR, "internal error\n");
      }
    }

    /**
     * HTTP/2 router (por stream)
     */
    void handleHttp2(final ChannelHandlerContext ctx, final Http2RequestState st) {
      try {
        if (st.path == null || !st.path.startsWith(DOH_PATH)) {
          sendHttp2Text(ctx, st, NOT_FOUND, "not found\n");
          return;
        }

        if ("GET".equals(st.method)) {
          final var dnsQueryBytes = DohCodec.parseGetDnsParam(st.path);
          if (dnsQueryBytes == null) {
            sendHttp2Text(ctx, st, BAD_REQUEST, "missing or invalid dns param\n");
            return;
          }
          final var dnsResponseBytes = dnsHandler.handle(dnsQueryBytes);
          sendHttp2Dns(ctx, st, OK, dnsResponseBytes);
          return;
        }

        if ("POST".equals(st.method)) {
          if (st.contentType == null || !st.contentType.startsWith(DOH_MEDIA_TYPE)) {
            sendHttp2Text(ctx, st, UNSUPPORTED_MEDIA_TYPE,
                "content-type must be application/dns-message\n"
            );
            return;
          }

          final var dnsQueryBytes = st.body.toByteArray();
          if (dnsQueryBytes.length == 0) {
            sendHttp2Text(ctx, st, BAD_REQUEST, "empty body\n");
            return;
          }

          final var dnsResponseBytes = dnsHandler.handle(dnsQueryBytes);
          sendHttp2Dns(ctx, st, OK, dnsResponseBytes);
          return;
        }

        sendHttp2Text(ctx, st, METHOD_NOT_ALLOWED, "method not allowed\n");
      } catch (final Exception e) {
        sendHttp2Text(ctx, st, INTERNAL_SERVER_ERROR, "internal error\n");
      }
    }

    // ---------------- HTTP/1 response helpers ----------------

    private static void sendHttp1Dns(final ChannelHandlerContext ctx,
        final HttpResponseStatus status, final byte[] dnsBytes) {
      final var content = Unpooled.wrappedBuffer(dnsBytes);
      final var res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);

      res.headers()
          .set(CONTENT_TYPE, DOH_MEDIA_TYPE);
      res.headers()
          .setInt(CONTENT_LENGTH, dnsBytes.length);
      res.headers()
          .set(CONNECTION, CLOSE);
      // recomendado por DoH (evita proxies fazendo besteira):
      res.headers()
          .set(CACHE_CONTROL, "no-store");

      ctx.writeAndFlush(res)
          .addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendHttp1Text(final ChannelHandlerContext ctx,
        final HttpResponseStatus status, final String body) {
      final var bytes = body.getBytes(StandardCharsets.UTF_8);
      final var content = Unpooled.wrappedBuffer(bytes);
      final var res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);

      res.headers()
          .set(CONTENT_TYPE, "text/plain; charset=utf-8");
      res.headers()
          .setInt(CONTENT_LENGTH, bytes.length);
      res.headers()
          .set(CONNECTION, CLOSE);
      res.headers()
          .set(CACHE_CONTROL, "no-store");

      ctx.writeAndFlush(res)
          .addListener(ChannelFutureListener.CLOSE);
    }

    // ---------------- HTTP/2 response helpers ----------------

    private static void sendHttp2Dns(final ChannelHandlerContext ctx, final Http2RequestState st,
        final HttpResponseStatus status, final byte[] dnsBytes) {
      final var headers = new DefaultHttp2Headers()
          .status(String.valueOf(status.code()))
          .set("content-type", DOH_MEDIA_TYPE)
          .set("cache-control", "no-store");

      ctx.write(new DefaultHttp2HeadersFrame(headers, false));
      ctx.writeAndFlush(new DefaultHttp2DataFrame(Unpooled.wrappedBuffer(dnsBytes), true));
    }

    private static void sendHttp2Text(final ChannelHandlerContext ctx, final Http2RequestState st
        , final HttpResponseStatus status, final String body) {
      final var bytes = body.getBytes(StandardCharsets.UTF_8);
      final var headers = new DefaultHttp2Headers()
          .status(String.valueOf(status.code()))
          .set("content-type", "text/plain; charset=utf-8")
          .set("cache-control", "no-store");

      ctx.write(new DefaultHttp2HeadersFrame(headers, false));
      ctx.writeAndFlush(new DefaultHttp2DataFrame(Unpooled.wrappedBuffer(bytes), true));
    }

    private static String headerLower(final HttpHeaders headers, final CharSequence name) {
      final var v = headers.get(name);
      return v == null ? null : v.toLowerCase(Locale.ROOT);
    }
  }

  // ---------------------------------------------------------------------------
  // Netty plumbing: ALPN -> HTTP/1 or HTTP/2
  // ---------------------------------------------------------------------------
  private static final class AlpnNegotiationHandler extends ApplicationProtocolNegotiationHandler {

    private final HttpRouteHandlers routes;

    private AlpnNegotiationHandler(final DnsMessageHandler dnsHandler) {
      super(ApplicationProtocolNames.HTTP_1_1);
      this.routes = new HttpRouteHandlers(dnsHandler);
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
          routes.handleHttp1(c, req);
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext c, final Throwable cause) {
          cause.printStackTrace();
          c.close();
        }
      });
    }

    private void configureHttp2(final ChannelHandlerContext ctx) {
      final var p = ctx.pipeline();

      final var frameCodec = Http2FrameCodecBuilder.forServer()
          .build();
      final var childHandler = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(final Channel ch) {
          ch.pipeline()
              .addLast(new Http2StreamHandler(routes));
        }
      };

      p.addLast(frameCodec);
      p.addLast(new Http2MultiplexHandler(childHandler));
    }
  }

  // ---------------------------------------------------------------------------
  // HTTP/2 per-stream state + handler (acumula body e chama routes)
  // ---------------------------------------------------------------------------
  static final class Http2RequestState {
    String method;
    String path;
    String contentType;
    final ByteArrayOutputStream body = new ByteArrayOutputStream(512);
  }

  private static final class Http2StreamHandler extends SimpleChannelInboundHandler<Http2Frame> {

    private final HttpRouteHandlers routes;
    private final Http2RequestState st = new Http2RequestState();
    private boolean sawHeaders;

    private Http2StreamHandler(final HttpRouteHandlers routes) {
      this.routes = routes;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Http2Frame frame)
        throws Exception {
      if (frame instanceof Http2HeadersFrame headersFrame) {
        sawHeaders = true;
        final var headers = headersFrame.headers();

        st.method = headers.method() == null ? null : headers.method()
            .toString();
        st.path = headers.path() == null ? null : headers.path()
            .toString();
        st.contentType = headers.get("content-type") == null ? null : headers.get("content-type")
            .toString()
            .toLowerCase(Locale.ROOT);

        if (headersFrame.isEndStream()) {
          routes.handleHttp2(ctx, st);
        }
        return;
      }

      if (frame instanceof Http2DataFrame dataFrame) {
        final var bytes = ByteBufs.toByteArray(dataFrame.content());
        st.body.write(bytes);

        if (sawHeaders && dataFrame.isEndStream()) {
          routes.handleHttp2(ctx, st);
        }
      }

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }
  }

  // ---------------------------------------------------------------------------
  // DoH codec (GET dns=base64url / POST body)
  // ---------------------------------------------------------------------------
  static final class DohCodec {

    /**
     * /dns-query?dns=BASE64URL
     */
    static byte[] parseGetDnsParam(final String rawPathOrUri) {
      try {
        final var uri = rawPathOrUri.startsWith("http")
            ? URI.create(rawPathOrUri)
            : URI.create("https://localhost" + rawPathOrUri);

        final var query = uri.getRawQuery();
        if (query == null || query.isBlank()) {
          return null;
        }

        final var params = parseQuery(query);
        final var dns = params.get("dns");
        if (dns == null || dns.isBlank()) {
          return null;
        }

        return base64UrlDecodeNoPadding(dns);
      } catch (final Exception e) {
        return null;
      }
    }

    private static Map<String, String> parseQuery(final String rawQuery) {
      final var m = new TreeMap<String, String>();
      final var parts = rawQuery.split("&");
      for (final var part : parts) {
        final var eq = part.indexOf('=');
        if (eq <= 0) {
          continue;
        }
        final var k = urlDecode(part.substring(0, eq));
        final var v = urlDecode(part.substring(eq + 1));
        m.put(k, v);
      }
      return m;
    }

    private static String urlDecode(final String s) {
      // bem simples e suficiente pra dns= (base64url não usa % geralmente)
      return s.replace("+", "%2B")
          .replace("%2F", "/");
    }

    private static byte[] base64UrlDecodeNoPadding(final String s) {
      // Base64 URL sem padding é o comum em DoH GET
      var v = s.trim();
      final var mod = v.length() % 4;
      if (mod == 2) {
        v = v + "==";
      } else if (mod == 3) {
        v = v + "=";
      } else if (mod != 0) {
        return null;
      }

      return Base64.getUrlDecoder()
          .decode(v);
    }
  }

  // ---------------------------------------------------------------------------
  // ByteBuf helpers
  // ---------------------------------------------------------------------------
  static final class ByteBufs {
    static byte[] toByteArray(final ByteBuf buf) {
      final var bytes = new byte[buf.readableBytes()];
      final var idx = buf.readerIndex();
      buf.getBytes(idx, bytes);
      return bytes;
    }
  }

  // ---------------------------------------------------------------------------
  // TLS / ALPN (native-image friendly)
  // ---------------------------------------------------------------------------
  private static SslContext buildSslContext(final InputStream p12, final char[] password)
      throws Exception {
    final var ks = KeyStore.getInstance("PKCS12");
    ks.load(p12, password);

    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, password);

    return SslContextBuilder
        .forServer(kmf)
        .sslProvider(SslProvider.JDK)
        .applicationProtocolConfig(new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.ALPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            ApplicationProtocolNames.HTTP_2,
            ApplicationProtocolNames.HTTP_1_1
        ))
        .build();
  }

  public static Channel start(
      RequestHandler requestHandler, InetSocketAddress address
  ) {
    try {
      return start0(requestHandler, address);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static Channel start0(
      RequestHandler requestHandler, InetSocketAddress address
  ) throws Exception {

    final var in = IoUtils.getResourceAsStream("/META-INF/resources/doh-server.p12");
    final var password = "changeit".toCharArray();

    return new Server(
        address, in, password, mapHandler(requestHandler)
    ).start();
  }

  static DnsMessageHandler mapHandler(RequestHandler requestHandler) {
    return bytes -> requestHandler.handle(Messages.of(bytes), "doh")
        .toWire();
  }
}
