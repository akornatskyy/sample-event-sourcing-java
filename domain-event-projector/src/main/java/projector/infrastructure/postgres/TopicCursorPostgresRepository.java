package projector.infrastructure.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projector.core.TopicCursorRepository;

public class TopicCursorPostgresRepository implements TopicCursorRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(
      TopicCursorPostgresRepository.class);

  @Override
  public String getCursor(String topic) {
    LOGGER.info("get cursor for {}", topic);
    return null;
  }

  @Override
  public void upsertCursor(String topic, String cursor) {
    LOGGER.info("upsert cursor for {} to {}", topic, cursor);
  }
}
