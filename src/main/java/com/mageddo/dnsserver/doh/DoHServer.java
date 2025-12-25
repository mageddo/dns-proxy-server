package com.mageddo.dnsserver.doh;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.HttpHeaders;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.mageddo.http.HttpStatus;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import org.xbill.DNS.Message;
import org.xbill.DNS.Type;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;

/**
 * See
 * https://chatgpt.com/g/g-p-6942b7c71414819185e2a851e7e1ae9d-dps/c/694c9615-fdec-8326-8024
 * -68d316bae4cb
 */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DoHServer implements AutoCloseable {

  /**
   * RFC 8484 media type
   */
  private static final String DNS_MESSAGE = "application/dns-message";

  private final RequestHandlerDefault requestHandler;
  private HttpsServer server;
  private volatile boolean started;

  @SneakyThrows
  public void start(InetAddress addr, int port) {

    synchronized (this) {
      if (this.started) {
        throw new IllegalStateException("Server already started");
      }
      this.started = true;
    }

    this.server = HttpsServer.create(new InetSocketAddress(addr, port), 0);
    this.server.setHttpsConfigurator(new HttpsConfigurator(buildSslContext()));

    final var resolver = new DnsResolver() {
      public byte[] resolve(final byte[] bytes) {

        final var query = Messages.of(bytes);
//        final var hostname = Messages.findQuestionHostname(query);
//        final var code = Messages.findQuestionTypeCode(query);
//
//        log.debug("status=begin, code={}, hostname={}", code, hostname);
        final var res = requestHandler.handle(query, "doH");
//        if (debug(query)) {
//          log.debug(
//              "status=match, code={}, hostname={}, query={}, res={}",
//              code, hostname, query, res
//          );
//        }
        return res.toWire();
      }

      private static boolean debug(Message query) {
        final var code = Messages.findQuestionTypeCode(query);
        return (query.toString()
            .contains("nginx-2.dev") || query.toString()
            .contains("mageddo.com") || query.toString()
            .contains("nginx-2.docker")
        ) && Set.of(Type.A, Type.HTTPS)
            .contains(code);
      }
    };

    this.server.createContext("/dns-query", exchange -> handleDnsQuery(exchange, resolver));
    this.server.createContext("/health", DoHServer::handleHealth);

    this.server.setExecutor(null); // default executor
    this.server.start();
    log.info("status=starting, address={}, port={}", addr, port);
  }

  // -------------------------
  // Endpoints
  // -------------------------

  static void handleDnsQuery(final HttpExchange exchange, final DnsResolver resolver) {
    try (exchange) {

      try {

        final var method = exchange.getRequestMethod();
        final var requestHeaders = exchange.getRequestHeaders();

        final byte[] requestBytes = switch (method) {
          case POST -> readPostDnsMessage(exchange, requestHeaders);
          case GET -> readGetDnsMessage(exchange.getRequestURI());
          default -> {
            sendText(exchange, HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
            yield null;
          }
        };

        if (requestBytes == null) {
          return; // resposta já enviada
        }

        // Aqui você já tem os bytes DNS do request (wire format RFC 1035)
        final var responseBytes = resolver.resolve(requestBytes);

        if (responseBytes == null || responseBytes.length == 0) {
          sendText(exchange, HttpStatus.BAD_GATEWAY, "Bad Gateway (empty DNS response)");
          return;
        }

        final var responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, DNS_MESSAGE);
        responseHeaders.set(HttpHeaders.CACHE_CONTROL, "no-store");
        responseHeaders.set("X-Content-Type-Options", "nosniff");

        exchange.sendResponseHeaders(HttpStatus.OK, responseBytes.length);
        try (final OutputStream os = exchange.getResponseBody()) {
          os.write(responseBytes);
        }
      } catch (final IllegalArgumentException e) {
        // base64 inválido, query param inválido, etc.
        sendText(exchange, HttpStatus.BAD_REQUEST, "Bad Request: " + e.getMessage());
      } catch (final Exception e) {
        // falha interna
        sendText(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
      }
    }
  }

  static void handleHealth(final HttpExchange exchange) {
    try (exchange) {
      if (!Objects.equals(exchange.getRequestMethod(), "GET")) {
        sendText(exchange, HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        return;
      }
      sendText(exchange, HttpStatus.OK, "ok");
    }
  }

  // -------------------------
  // Request parsing
  // -------------------------

  static byte[] readPostDnsMessage(final HttpExchange exchange, final Headers headers) {
    try {
      final var contentType = firstHeader(headers, HttpHeaders.CONTENT_TYPE);
      if (contentType == null || !contentType.toLowerCase()
          .startsWith(DNS_MESSAGE)) {
        // Muitos clientes mandam exatamente application/dns-message
        sendText(
            exchange,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "Unsupported Media Type (expected " + DNS_MESSAGE + ")"
        );
        return null;
      }

      try (final var is = exchange.getRequestBody()) {
        final var bytes = is.readAllBytes();
        if (bytes.length == 0) {
          sendText(exchange, 400, "Empty body");
          return null;
        }
        return bytes; // <-- BYTES DNS (wire format), prontos pra parsear
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static byte[] readGetDnsMessage(final URI uri) {
    final var params = parseQueryParams(uri);
    final var dnsParam = params.get("dns");
    if (dnsParam == null || dnsParam.isBlank()) {
      throw new IllegalArgumentException("missing 'dns' query param");
    }

    final var decoded = base64UrlNoPaddingDecode(dnsParam);
    if (decoded.length == 0) {
      throw new IllegalArgumentException("empty dns after decode");
    }
    return decoded; // <-- BYTES DNS (wire format), prontos pra parsear
  }

  private static Map<String, String> parseQueryParams(final URI uri) {
    final var rawQuery = uri.getRawQuery(); // não decodifica + nem % automaticamente
    final var map = new HashMap<String, String>();
    if (rawQuery == null || rawQuery.isBlank()) {
      return map;
    }

    final var pairs = rawQuery.split("&");
    for (final var pair : pairs) {
      if (pair.isBlank()) {
        continue;
      }

      final var idx = pair.indexOf('=');
      final var key = (idx >= 0) ? urlDecode(pair.substring(0, idx)) : urlDecode(pair);
      final var val = (idx >= 0) ? urlDecode(pair.substring(idx + 1)) : "";
      map.put(key, val);
    }
    return map;
  }

  private static String urlDecode(final String s) {
    // Sem libs: decode básico de %XX (suficiente pro dns param normalmente).
    // Se você quiser full decoder, dá pra usar java.net.URLDecoder (mas ele trata '+' como espaço).
    final var sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      final var c = s.charAt(i);
      if (c == '%' && i + 2 < s.length()) {
        final var hex = s.substring(i + 1, i + 3);
        sb.append((char) Integer.parseInt(hex, 16));
        i += 2;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static byte[] base64UrlNoPaddingDecode(final String base64Url) {
    // RFC 8484 (GET) usa base64url sem padding.
    // Java pode exigir múltiplo de 4, então completamos com '='.
    final var normalized = base64Url.replace('-', '+')
        .replace('_', '/');
    final var mod = normalized.length() % 4;
    final var padded = switch (mod) {
      case 0 -> normalized;
      case 2 -> normalized + "==";
      case 3 -> normalized + "=";
      default -> throw new IllegalArgumentException("invalid base64url length");
    };
    return Base64.getDecoder()
        .decode(padded);
  }

  private static String firstHeader(final Headers headers, final String name) {
    final var values = headers.get(name);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.getFirst();
  }

  static void sendText(final HttpExchange exchange, final int status, final String msg) {
    try {
      final var bytes = msg.getBytes(StandardCharsets.UTF_8);
      final var headers = exchange.getResponseHeaders();
      headers.set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
      headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
      exchange.sendResponseHeaders(status, bytes.length);
      try (final var os = exchange.getResponseBody()) {
        os.write(bytes);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static SSLContext buildSslContext() {
    return SslContextMapper.of("/META-INF/resources/doh-server.p12", "changeit".toCharArray());
  }

  @Override
  public void close() {
    if (this.server != null) {
      this.server.stop(0);
    }
  }

  // -------------------------
  // Interfaces / DNS wire helper
  // -------------------------

  public interface DnsResolver {
    byte[] resolve(byte[] dnsQueryBytes);
  }

  /**
   * Helper MINIMALISTA: cria uma resposta SERVFAIL copiando o ID do request.
   * Isso é só pra testar o pipeline end-to-end.
   * Você vai substituir pela tua implementação real.
   */
  public static final class DnsWire {

    private DnsWire() {
    }

    public static byte[] servfail(final byte[] query) {
      if (query.length < 12) {
        // header DNS mínimo tem 12 bytes; se vier lixo, devolve vazio
        return new byte[0];
      }

      final var response = query.clone();

      // DNS Header:
      // [0..1] ID: mantém igual
      // [2..3] Flags: set QR=1 (response), RCODE=2 (SERVFAIL), limpa algumas flags
      // Isso é bem simplificado (não é um "resolver" real).
      final int flags = ((response[2] & 0xFF) << 8) | (response[3] & 0xFF);

      // força QR=1
      final int flagsWithQr = flags | 0x8000;
      // zera RCODE e seta SERVFAIL(2)
      final int flagsServfail = (flagsWithQr & 0xFFF0) | 0x0002;

      response[2] = (byte) ((flagsServfail >> 8) & 0xFF);
      response[3] = (byte) (flagsServfail & 0xFF);

      // zera contadores de resposta (AN/NS/AR = 0)
      response[6] = 0;
      response[7] = 0;  // ANCOUNT
      response[8] = 0;
      response[9] = 0;  // NSCOUNT
      response[10] = 0;
      response[11] = 0; // ARCOUNT

      return response;
    }
  }
}
