package subscriber.api.core;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import reactor.core.publisher.Flux;

public class SubscriberService {
  private final ScheduledExecutorService scheduledExecutor = Executors
      .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
  private final Supplier<LogConsumer> logConsumerSupplier;

  public SubscriberService(
      Supplier<LogConsumer> logConsumerSupplier) {
    this.logConsumerSupplier = logConsumerSupplier;
  }

  public Flux<Event> consume(
      Context ctx, Subscription subscription) {
    var consumer = this.logConsumerSupplier.get();
    consumer.assignAndSeek(subscription);
    var offset = subscription.offset;
    return Flux.create(sink -> scheduledExecutor.submit(new Runnable() {
      @Override
      public void run() {
        var entries = consumer.poll(Duration.ZERO);
        for (var entry : entries) {
          var event = new Event();
          event.id = offset.upsert(entry.partition, entry.offset).encode();
          event.type = entry.type;
          event.data = entry.value;
          sink.next(event);
        }

        if (!sink.isCancelled()) {
          if (entries.isEmpty()) {
            scheduledExecutor.schedule(this, 250, TimeUnit.MILLISECONDS);
          } else {
            scheduledExecutor.submit(this);
          }
        } else {
          consumer.close();
          sink.complete();
        }
      }
    }));
  }
}
