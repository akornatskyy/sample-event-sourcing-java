package outbox.core;

public interface DomainEventSerializer {
  byte[] serialize(DomainEvent<?> data);
}
