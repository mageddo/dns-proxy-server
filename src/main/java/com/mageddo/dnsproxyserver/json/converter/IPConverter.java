package com.mageddo.dnsproxyserver.json.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mageddo.net.IPI;

import java.io.IOException;

public class IPConverter {
  public static class Serializer extends JsonSerializer<IPI> {
    @Override
    public void serialize(IPI value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        gen.writeNull();
      } else {
        gen.writeString(value.toText());
      }
    }
  }

  public static class Deserializer extends JsonDeserializer<IPI> {
    @Override
    public IPI deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.currentToken() == null) {
        return null;
      }
      return IPI.of(p.getValueAsString());
    }
  }
}
