package subscriber.api.core;

import java.time.Duration;
import java.util.List;

public interface LogConsumer {
  void assignAndSeek(Subscription subscription);
  List<LogEntry> poll(Duration timeout);
  void close();
}
