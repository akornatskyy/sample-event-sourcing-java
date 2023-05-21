package publisher.infrastructure.kafka;

import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.utils.Utils;

public class Partitioner {
  private final int numberOfPartitions;

  public Partitioner(int numberOfPartitions) {
    this.numberOfPartitions = numberOfPartitions;
  }

  public int partition(String key) {
    return Utils.toPositive(Utils.murmur2(key.getBytes(StandardCharsets.UTF_8)))
           % numberOfPartitions;
  }
}