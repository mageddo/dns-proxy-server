package testing;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.di.Context;
import dagger.sheath.EventHandler;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;
import nativeimage.Reflection;

@Slf4j
@Reflection(scanClass = ContextSupplier.class)
public class Events implements EventHandler<Context> {
  @Override
  public void afterSetup(Context component) {

    final var config = Configs.getInstance();
    log.info("status=startingDPS, port={}", config.getWebServerPort());
    component.start();

    RestAssured.port = config.getWebServerPort();
    RestAssured.config = RestAssured
        .config()
        .httpClient(
            HttpClientConfig
                .httpClientConfig()
                .setParam("http.connect.timeout", 5_000)
                .setParam("http.socket.timeout", 5_000)
        );
  }

  @Override
  public void afterAll(Context component) {
    component.stop();
  }
}
