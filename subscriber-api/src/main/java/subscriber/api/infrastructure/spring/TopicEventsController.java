package subscriber.api.infrastructure.spring;

import java.util.List;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import subscriber.api.core.Context;
import subscriber.api.core.SubscriberService;
import subscriber.api.core.Subscription;
import subscriber.api.core.SubscriptionOffset;

@RestController
class TopicEventsController {
  private final SubscriberService service;

  public TopicEventsController(SubscriberService service) {
    this.service = service;
  }

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/topics/{topic}/events")
  @CrossOrigin
  public Flux<ServerSentEvent<String>> get(
      @RequestHeader(value = "x-app-id", required = false)
      String appId,
      @RequestHeader(value = "x-last-event-id", required = false)
      String lastEventId,
      @PathVariable(value = "topic")
      String topic,
      @RequestParam(value = "eventTypes", required = false)
      List<String> eventTypes) {

    var ctx = new Context();
    ctx.appId = appId;

    var subscription = new Subscription();
    subscription.topic = topic;
    subscription.eventTypes = eventTypes;
    subscription.offset = SubscriptionOffset.decode(lastEventId);

    return service.consume(ctx, subscription)
        .map((d) -> ServerSentEvent.<String>builder()
            .id(d.id)
            .event(d.type)
            .data(d.data)
            .build());
  }
}
