package projector;

import java.util.List;
import projector.core.Subscription;
import projector.infrastructure.Factory;

public class Application {
  public static void main(String[] args) {
    var factory = new Factory();
    var projector = factory.createProjector();

    var subscription = new Subscription();
    subscription.topic = "quickstart-events";
    subscription.eventTypes = List.of("UpdateCustomer");

    projector.spawn(subscription);
  }
}