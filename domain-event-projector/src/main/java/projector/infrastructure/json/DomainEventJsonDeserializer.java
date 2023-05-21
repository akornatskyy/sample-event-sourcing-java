package projector.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import projector.core.DomainEvent;
import projector.core.DomainEventDeserializer;
import projector.core.events.UpdateCustomerEvent;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    property = "type")
abstract class DomainEventMixIn {
}

public class DomainEventJsonDeserializer implements DomainEventDeserializer {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  static {
    MAPPER.addMixIn(DomainEvent.class, DomainEventMixIn.class);
    MAPPER.registerModule(
        new SimpleModule()
            .registerSubtypes(
                new NamedType(UpdateCustomerEvent.class, "UpdateCustomer")));
  }

  public <T extends DomainEvent<?>> T deserialize(String data) {
    try {
      return MAPPER.readValue(data, new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
