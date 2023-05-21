package projector.core;

public final class Event {
  public String id;
  public String type;
  public String data;

  @Override
  public String toString() {
    return "Event{" +
           "id='" + id + '\'' +
           ", type='" + type + '\'' +
           '}';
  }
}
