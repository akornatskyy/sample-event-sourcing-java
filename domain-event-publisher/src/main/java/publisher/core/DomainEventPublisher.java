package publisher.core;

import java.util.concurrent.CompletableFuture;

public interface DomainEventPublisher {
  <T> CompletableFuture<Void> publish(String key, DomainEvent<T> event);
}
