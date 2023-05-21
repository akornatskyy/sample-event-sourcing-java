package projector.core;

import java.io.Closeable;

public interface UnitOfWork extends Closeable {
  TopicCursorRepository cursor();

  CustomerRepository customer();

  void commit();

  void close();
}
