package subscriber.api.infrastructure.kafka;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import subscriber.api.core.LogConsumer;
import subscriber.api.core.LogEntry;
import subscriber.api.core.Subscription;

public class LogKafkaConsumer implements LogConsumer {
  private final KafkaConsumer<String, String> kafkaConsumer;
  private List<String> eventTypes;

  public LogKafkaConsumer(
      KafkaConsumer<String, String> kafkaConsumer) {
    this.kafkaConsumer = kafkaConsumer;
  }

  public void assignAndSeek(Subscription subscription) {
    eventTypes = subscription.eventTypes;
    var subscriptionOffset = subscription.offset;
    var partitions = kafkaConsumer.partitionsFor(subscription.topic)
        .stream()
        .map(p -> new TopicPartition(p.topic(), p.partition()))
        .toList();
    kafkaConsumer.assign(partitions);
    var seekToBeginningPartitions = new ArrayList<TopicPartition>(
        partitions.size());
    for (var p : partitions) {
      var partitionOffset = subscriptionOffset.findPartitionOffset(
          p.partition());
      if (partitionOffset.isPresent()) {
        kafkaConsumer.seek(p, partitionOffset.get() + 1);
      } else {
        seekToBeginningPartitions.add(p);
      }
    }

    if (!seekToBeginningPartitions.isEmpty()) {
      kafkaConsumer.seekToBeginning(seekToBeginningPartitions);
    }
  }

  public List<LogEntry> poll(Duration timeout) {
    var records = kafkaConsumer.poll(timeout);
    List<LogEntry> events = new ArrayList<>();
    for (var r : records) {
      var header = r.headers().lastHeader("event-type");
      if (header == null) {
        continue;
      }

      var eventType = new String(header.value(), StandardCharsets.UTF_8);
      if (eventTypes != null && !eventTypes.contains(eventType)) {
        continue;
      }

      var entry = new LogEntry();
      entry.partition = r.partition();
      entry.offset = r.offset();
      entry.type = eventType;
      entry.value = r.value();
      events.add(entry);
    }

    return events;
  }

  @Override
  public void close() {
    this.kafkaConsumer.close();
  }
}
