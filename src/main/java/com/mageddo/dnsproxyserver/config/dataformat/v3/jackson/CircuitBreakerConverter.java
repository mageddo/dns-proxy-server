package com.mageddo.dnsproxyserver.config.dataformat.v3.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.NonResilientCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3.StaticThreshold;
import com.mageddo.json.JsonUtils;

import org.apache.commons.lang3.EnumUtils;

public interface CircuitBreakerConverter {

  class Serializer extends JsonSerializer<CircuitBreakerStrategyConfig> {
    @Override
    public void serialize(CircuitBreakerStrategyConfig o, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
        throws IOException {
      if (o == null) {
        jsonGenerator.writeNull();
      }
      jsonGenerator.writeObject(o);
    }
  }

  class Deserializer extends JsonDeserializer<CircuitBreakerStrategyConfig> {

    @Override
    public CircuitBreakerStrategyConfig deserialize(
        JsonParser jsonParser, DeserializationContext ctx
    ) throws IOException {
      if (jsonParser.currentToken() == JsonToken.VALUE_NULL) {
        return null;
      }
      final JsonNode node = jsonParser.readValueAsTree();
      final var typeName = node.at("/type")
          .asText();
      final var type = EnumUtils.getEnum(
          CircuitBreakerStrategyConfig.Type.class,
          typeName,
          CircuitBreakerStrategyConfig.Type.CANARY_RATE_THRESHOLD
      );
      return switch (type) {
        case CANARY_RATE_THRESHOLD -> readAs(ctx, node, ConfigV3.CanaryRateThreshold.class);
        case STATIC_THRESHOLD -> readAs(ctx, node, StaticThreshold.class);
        case NON_RESILIENT -> new NonResilientCircuitBreakerStrategyConfig();
      };
    }

    static <T> T readAs(
        DeserializationContext ctx, JsonNode node, Class<T> type
    ) {
      return JsonUtils.readValue(node.toPrettyString(), type);
    }
  }
}
