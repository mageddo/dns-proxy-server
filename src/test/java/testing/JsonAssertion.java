package testing;

import com.mageddo.json.JsonUtils;
import io.restassured.path.json.JsonPath;

public class JsonAssertion {
  public static JsonPath jsonPath(String json){
    return new JsonPath(json);
  }

  public static JsonPath jsonPath(Object o) {
    return jsonPath(JsonUtils.writeValueAsString(o));
  }
}
