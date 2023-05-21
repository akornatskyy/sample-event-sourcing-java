package projector.infrastructure.http;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import projector.core.Event;
import projector.core.EventSubscriber;
import projector.core.Subscription;
import reactor.core.publisher.Flux;

public class EventHTTPSubscriber implements EventSubscriber {
  private final WebClient client;

  public EventHTTPSubscriber(WebClient client) {
    this.client = client;
  }

  @Override
  public Flux<Event> subscribe(Subscription subscription) {
    var uri = UriComponentsBuilder
        .fromUriString("http://localhost:8080")
        .path(String.format("/topics/%s/events", subscription.topic))
        .queryParam("eventTypes", subscription.eventTypes)
        .toUriString();
    System.out.println(subscription);
    return client.get()
        .uri(uri)
        .header("x-last-event-id", subscription.cursor)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
        })
        .map(sse -> {
          var event = new Event();
          event.id = sse.id();
          event.type = sse.event();
          event.data = sse.data();
          return event;
        });
  }
}
