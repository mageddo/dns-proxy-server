package com.mageddo.dnsserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import lombok.RequiredArgsConstructor;

/**
 * See
 * https://chatgpt.com/g/g-p-6942b7c71414819185e2a851e7e1ae9d-dps/c/694c9615-fdec-8326-8024
 * -68d316bae4cb
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DoHServer {

  // RFC 8484 media type
  private static final String DNS_MESSAGE = "application/dns-message";

  private final RequestHandlerDefault requestHandler;


  public void start(InetAddress addr, int port) {
//    this.start0(protocol, addr, port);

    final var port = 8443;
    final var keystorePath = "doh.p12";
    final var storePass = "changeit".toCharArray();

    final var sslContext = buildSslContext(keystorePath, storePass);

    final var server = HttpsServer.create(new InetSocketAddress("0.0.0.0", port), 0);
    server.setHttpsConfigurator(new HttpsConfigurator(sslContext));

    final var resolver = new DnsResolver() {
      @Override
      public byte[] resolve(final byte[] dnsQueryBytes) {
        // TODO: aqui você faz o parse da mensagem DNS (wire format) e gera a resposta.
        // Retornando um SERVFAIL mínimo só pra "fechar o loop".
        return DnsWire.servfail(dnsQueryBytes);
      }
    };

    server.createContext("/dns-query", exchange -> handleDnsQuery(exchange, resolver));
    server.createContext("/health", exchange -> handleHealth(exchange));

    server.setExecutor(null); // default executor
    server.start();

    System.out.println("DoH server listening on https://localhost:" + port + "/dns-query");
  }

  // -------------------------
  // Endpoints
  // -------------------------

  private static void handleDnsQuery(final HttpExchange exchange, final DnsResolver resolver)
      throws IOException {
    try (exchange) {
      final var method = exchange.getRequestMethod();
      final var requestHeaders = exchange.getRequestHeaders();

      final byte[] requestBytes = switch (method) {
        case "POST" -> readPostDnsMessage(exchange, requestHeaders);
        case "GET" -> readGetDnsMessage(exchange.getRequestURI());
        default -> {
          sendText(exchange, 405, "Method Not Allowed");
          yield null;
        }
      };

      if (requestBytes == null) {
        return; // resposta já enviada
      }

      // Aqui você já tem os bytes DNS do request (wire format RFC 1035)
      final var responseBytes = resolver.resolve(requestBytes);

      if (responseBytes == null || responseBytes.length == 0) {
        sendText(exchange, 502, "Bad Gateway (empty DNS response)");
        return;
      }

      final var responseHeaders = exchange.getResponseHeaders();
      responseHeaders.set("Content-Type", DNS_MESSAGE);
      responseHeaders.set("Cache-Control", "no-store");
      // opcional/útil:
      responseHeaders.set("X-Content-Type-Options", "nosniff");

      exchange.sendResponseHeaders(200, responseBytes.length);
      try (final OutputStream os = exchange.getResponseBody()) {
        os.write(responseBytes);
      }
    } catch (final IllegalArgumentException e) {
      // base64 inválido, query param inválido, etc.
      sendText(exchange, 400, "Bad Request: " + e.getMessage());
    } catch (final Exception e) {
      // falha interna
      sendText(exchange, 500, "Internal Server Error");
    }
  }

  private static void handleHealth(final HttpExchange exchange) throws IOException {
    try (exchange) {
      if (!Objects.equals(exchange.getRequestMethod(), "GET")) {
        sendText(exchange, 405, "Method Not Allowed");
        return;
      }
      sendText(exchange, 200, "ok");
    }
  }

  // -------------------------
  // Request parsing
  // -------------------------

  private static byte[] readPostDnsMessage(final HttpExchange exchange, final Headers headers)
      throws IOException {
    final var contentType = firstHeader(headers, "Content-Type");
    if (contentType == null || !contentType.toLowerCase()
        .startsWith(DNS_MESSAGE)) {
      // Muitos clientes mandam exatamente application/dns-message
      sendText(exchange, 415, "Unsupported Media Type (expected " + DNS_MESSAGE + ")");
      return null;
    }

    try (final InputStream is = exchange.getRequestBody()) {
      final var bytes = is.readAllBytes();
      if (bytes.length == 0) {
        sendText(exchange, 400, "Empty body");
        return null;
      }
      return bytes; // <-- BYTES DNS (wire format), prontos pra parsear
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

  // -------------------------
  // Response helpers
  // -------------------------

  private static void sendText(final HttpExchange exchange, final int status, final String msg)
      throws IOException {
    final var bytes = msg.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders()
        .set("Content-Type", "text/plain; charset=utf-8");
    exchange.getResponseHeaders()
        .set("Cache-Control", "no-store");
    exchange.sendResponseHeaders(status, bytes.length);
    try (final OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  // -------------------------
  // TLS
  // -------------------------

  private static SSLContext buildSslContext(final String pkcs12Path, final char[] password)
      throws Exception {
    final var ks = KeyStore.getInstance("PKCS12");
    try (final var is = DoHServer.class.getClassLoader()
        .getResourceAsStream(pkcs12Path)) {
      if (is != null) {
        ks.load(is, password);
      } else {
        try (final var fis = Files.newInputStream(Path.of(pkcs12Path))) {
          ks.load(fis, password);
        }
      }
    }

    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, password);

    final var ctx = SSLContext.getInstance("TLS");
    ctx.init(kmf.getKeyManagers(), null, null);
    return ctx;
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
