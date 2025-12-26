//package com.mageddo.dnsserver.doh;
//
//import io.helidon.common.configurable.Resource;
//import io.helidon.webserver.WebServer;
//import io.helidon.webserver.http.HttpRouting;
//
//public class DohServerHelidon {
//  public static void main(String[] args) {
//    WebServer.builder()
//        .port(8444)
//        .tls(tls -> tls
//            .privateKey(key -> key
//                .keystore(store -> store.passphrase("changeit".toCharArray())
//                    .keystore(Resource.create("META-INF/resources/doh-server.p12"))
//                ))
//        )
//        .addRouting(HttpRouting.builder()
//            .get("/dns-query", (req, res) -> {
//                  res.send("Resposta DNS");
//                }
//            ))
//        .build()
//        .start();
//  }
//}
