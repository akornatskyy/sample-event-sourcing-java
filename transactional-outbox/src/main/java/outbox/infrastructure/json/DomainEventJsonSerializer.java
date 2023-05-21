package outbox.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import outbox.core.DomainEvent;
import outbox.core.DomainEventSerializer;

public class DomainEventJsonSerializer implements DomainEventSerializer {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Override
  public byte[] serialize(DomainEvent<?> data) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
