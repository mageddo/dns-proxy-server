package testing;

import com.mageddo.dnsproxyserver.di.Context;
import dagger.sheath.EventHandler;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Events implements EventHandler<Context> {
  @Override
  public void afterSetup(Context component) {
    log.info("status=startingDPS");
    component.start();
    RestAssured.port = 5380;
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
