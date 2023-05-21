package outbox.infrastructure.kafka;

import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.utils.Utils;
import outbox.core.Partitioner;

public class KafkaPartitioner implements Partitioner {
  private final int numberOfPartitions;

  public KafkaPartitioner(int numberOfPartitions) {
    this.numberOfPartitions = numberOfPartitions;
  }

  @Override
  public int partition(String key) {
    return Utils.toPositive(Utils.murmur2(key.getBytes(StandardCharsets.UTF_8)))
           % numberOfPartitions;
  }
}