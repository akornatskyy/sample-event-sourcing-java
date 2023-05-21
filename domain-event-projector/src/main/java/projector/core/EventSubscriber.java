package projector.core;

import reactor.core.publisher.Flux;

public interface EventSubscriber {
  Flux<Event> subscribe(Subscription subscription);
}
