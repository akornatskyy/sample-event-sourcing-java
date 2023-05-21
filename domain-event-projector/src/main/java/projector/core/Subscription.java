package projector.core;

import java.util.List;

public class Subscription {
  public String cursor;
  public String topic;
  public List<String> eventTypes;

  @Override
  public String toString() {
    return "Subscription{" +
           "cursor='" + cursor + '\'' +
           ", topic='" + topic + '\'' +
           ", eventTypes=" + eventTypes +
           '}';
  }
}
