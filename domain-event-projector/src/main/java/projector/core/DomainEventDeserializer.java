package projector.core;

public interface DomainEventDeserializer {
  <T extends DomainEvent<?>> T deserialize(String data);
}
