# Sample Event Sourcing Java

[![tests](https://github.com/akornatskyy/sample-event-sourcing-java/actions/workflows/tests.yaml/badge.svg)](https://github.com/akornatskyy/sample-event-sourcing-java/actions/workflows/tests.yaml)

## Setup

Create *quickstart-events* topic.

```sh
bin/kafka-topics.sh --create --topic quickstart-events \
  --partitions 4 --bootstrap-server localhost:9092
```

Open consumer for this topic.

```sh
bin/kafka-console-consumer.sh --topic quickstart-events \
  --from-beginning --bootstrap-server localhost:9092
```

## Event Publisher

Run *domain-event-publisher* to emit *UpdateCustomer* and 
*ActivateCustomer* events.

## Transactional Outbox

Supposed to be used with *Change Data Capture* pattern to emit domain events.

TODO: requires SQL schema and testing.

## Subscriber API

Run *subscriber-api* to access events via HTTP SSE.

Receive all events from the beginning:

```sh
curl -v http://localhost:8080/topics/quickstart-events/events
```

Filter events by event type:

```sh
curl -v http://localhost:8080/topics/quickstart-events/events?eventTypes=UpdateCustomer
```

Request events using a cursor:

```sh
curl -v -H "x-last-event-id:MCwx" \
  http://localhost:8080/topics/quickstart-events/events
```

TODO: filter by *tenant-id*, multiple topics.

## Event Projector

Uses *Subscriber API* to read events and update local read store. Run
*domain-event-projector*.

TODO: SQL repository implementation.

## References

- [Event Sourcing Pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/event-sourcing)
- [Command–Query Separation](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation)
- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)