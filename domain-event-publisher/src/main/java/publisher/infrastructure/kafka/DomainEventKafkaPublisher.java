package publisher.infrastructure.kafka;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import publisher.core.DomainEvent;
import publisher.core.DomainEventPublisher;

public class DomainEventKafkaPublisher implements DomainEventPublisher {
  private final Producer<String, DomainEvent<?>> producer;
  private final Partitioner partitioner;
  private final String topic;

  public DomainEventKafkaPublisher(
      Producer<String, DomainEvent<?>> producer,
      Partitioner partitioner,
      String topic) {
    this.producer = producer;
    this.partitioner = partitioner;
    this.topic = topic;
  }

  public <T> CompletableFuture<Void> publish(String key, DomainEvent<T> event) {
    var promise = new CompletableFuture<RecordMetadata>();
    producer.send(
        new ProducerRecord<>(
            topic, partitioner.partition(key), key, event,
            List.of(new RecordHeader(
                "event-type", event.type.getBytes(StandardCharsets.UTF_8)))),
        (metadata, exception) -> {
          if (exception == null) {
            promise.complete(metadata);
          } else {
            promise.completeExceptionally(exception);
          }
        });
    return CompletableFuture.allOf(promise);
  }
}
