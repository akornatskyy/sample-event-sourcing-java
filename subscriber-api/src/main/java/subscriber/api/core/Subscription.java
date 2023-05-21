package subscriber.api.core;

import java.util.List;

public final class Subscription {
  public String topic;
  public List<String> eventTypes;
  public SubscriptionOffset offset;
}