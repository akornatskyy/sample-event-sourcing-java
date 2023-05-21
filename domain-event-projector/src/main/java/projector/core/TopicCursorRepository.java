package projector.core;

public interface TopicCursorRepository {
  String getCursor(String topic);
  void upsertCursor(String topic, String cursor);
}
