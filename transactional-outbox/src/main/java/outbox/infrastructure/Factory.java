package outbox.infrastructure;

import outbox.core.CustomerService;
import outbox.core.DomainEventSerializer;
import outbox.core.Partitioner;
import outbox.infrastructure.json.DomainEventJsonSerializer;
import outbox.infrastructure.kafka.KafkaPartitioner;
import outbox.infrastructure.postgres.CustomerPostgresRepository;
import outbox.infrastructure.postgres.OutboxPostgresRepository;
import outbox.infrastructure.sql.UnitOfSqlWork;

public class Factory {
  private static final DomainEventSerializer DOMAIN_EVENT_SERIALIZER
      = new DomainEventJsonSerializer();

  private final Partitioner partitioner = new KafkaPartitioner(4);

  public CustomerService createOrderService() {
    return new CustomerService(
        () -> new UnitOfSqlWork(
            "",
            OutboxPostgresRepository::new,
            CustomerPostgresRepository::new),
        DOMAIN_EVENT_SERIALIZER,
        partitioner);
  }
}
