package subscriber.api.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SubscriptionOffset {
  private static final String SEPARATOR = ";";

  public List<PartitionOffset> offsets;

  public Optional<Long> findPartitionOffset(int partition) {
    for (var po : offsets) {
      if (po.partition == partition) {
        return Optional.of(po.offset);
      }
    }

    return Optional.empty();
  }

  public SubscriptionOffset upsert(int partition, long offset) {
    for (var po : offsets) {
      if (po.partition == partition) {
        po.offset = offset;
        return this;
      }
    }

    var po = new PartitionOffset();
    po.partition = partition;
    po.offset = offset;
    offsets.add(po);
    return this;
  }

  public static SubscriptionOffset decode(String encoded) {
    var so = new SubscriptionOffset();
    if (encoded == null) {
      so.offsets = new ArrayList<>();
      return so;
    }

    so.offsets = Arrays.stream(
            new String(Base64.getDecoder().decode(encoded),
                       StandardCharsets.UTF_8)
                .split(SEPARATOR))
        .map(PartitionOffset::decode)
        .toList();
    return so;
  }

  public String encode() {
    return Base64.getEncoder()
        .encodeToString(
            offsets.stream()
                .map(PartitionOffset::encode)
                .collect(Collectors.joining(SEPARATOR))
                .getBytes(StandardCharsets.UTF_8));
  }
}