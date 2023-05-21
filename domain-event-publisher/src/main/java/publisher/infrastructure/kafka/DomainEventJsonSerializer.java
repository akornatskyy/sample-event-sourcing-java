package publisher.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import publisher.core.DomainEvent;

public class DomainEventJsonSerializer implements Serializer<DomainEvent<?>> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Override
  public byte[] serialize(String topic, DomainEvent<?> data) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
