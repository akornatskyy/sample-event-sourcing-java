package outbox.core;

import java.io.Closeable;

public interface UnitOfWork extends Closeable {
  OutboxRepository outbox();

  CustomerRepository customer();

  void commit();

  void close();
}
