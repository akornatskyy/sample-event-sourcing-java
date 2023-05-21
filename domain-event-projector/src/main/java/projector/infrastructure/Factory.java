package projector.infrastructure;

import org.springframework.web.reactive.function.client.WebClient;
import projector.core.Projector;
import projector.infrastructure.http.EventHTTPSubscriber;
import projector.infrastructure.json.DomainEventJsonDeserializer;
import projector.infrastructure.postgres.CustomerPostgresRepository;
import projector.infrastructure.postgres.TopicCursorPostgresRepository;
import projector.infrastructure.sql.UnitOfSqlWork;

public class Factory {

  public Projector createProjector() {
    return new Projector(
        new EventHTTPSubscriber(
            WebClient.builder()
                .build()),
        new DomainEventJsonDeserializer(),
        () -> new UnitOfSqlWork(
            new TopicCursorPostgresRepository(),
            new CustomerPostgresRepository()),
        new TopicCursorPostgresRepository());
  }
}
