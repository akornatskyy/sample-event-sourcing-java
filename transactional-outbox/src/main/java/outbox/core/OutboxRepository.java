package outbox.core;

import outbox.core.domain.AddOutboxEntry;

public interface OutboxRepository {
  void add(AddOutboxEntry entry);
}
