package projector.core;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projector.core.events.UpdateCustomerEvent;

public class Projector {
  private static final Logger LOGGER = LoggerFactory.getLogger(Projector.class);

  private final EventSubscriber subscriber;
  private final DomainEventDeserializer deserializer;
  private final Supplier<UnitOfWork> unitOfWorkSupplier;
  private final TopicCursorRepository topicCursorRepository;

  public Projector(
      EventSubscriber subscriber,
      DomainEventDeserializer deserializer,
      Supplier<UnitOfWork> unitOfWorkSupplier,
      TopicCursorRepository topicCursorRepository) {
    this.subscriber = subscriber;
    this.deserializer = deserializer;
    this.unitOfWorkSupplier = unitOfWorkSupplier;
    this.topicCursorRepository = topicCursorRepository;
  }

  public void spawn(Subscription subscription) {
    LOGGER.info("starting {}", subscription);

    if (subscription.cursor == null) {
      subscription.cursor = topicCursorRepository.getCursor(subscription.topic);
    }

    // TODO: retry when subscribe fails
    subscriber.subscribe(subscription)
        .map(event -> this.process(subscription, event))
        .map(event -> {
          subscription.cursor = event.id;
          return event;
        })
        .blockLast();
  }

  private Event process(Subscription subscription, Event event) {
    LOGGER.info("processing {}", event);
    try (var unitOfWork = unitOfWorkSupplier.get()) {
      if (event.type.equals("UpdateCustomer")) {
        UpdateCustomerEvent e = deserializer.deserialize(event.data);
        unitOfWork.customer().updateCustomer(e.data);
      }

      unitOfWork.cursor().upsertCursor(subscription.topic, event.id);
      unitOfWork.commit();
    }

    return event;
  }
}
