package outbox.core;

public interface Partitioner {
  int partition(String key);
}
