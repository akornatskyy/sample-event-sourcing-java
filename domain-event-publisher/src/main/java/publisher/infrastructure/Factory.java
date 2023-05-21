package publisher.infrastructure;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import publisher.core.DomainEventPublisher;
import publisher.core.CustomerService;
import publisher.infrastructure.kafka.DomainEventJsonSerializer;
import publisher.infrastructure.kafka.DomainEventKafkaPublisher;
import publisher.infrastructure.kafka.Partitioner;

public class Factory {

  public CustomerService createOrderService() {
    return new CustomerService(
        createPublisher());
  }

  private DomainEventPublisher createPublisher() {
    var properties = new Properties();
    properties.setProperty(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    properties.setProperty(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        DomainEventJsonSerializer.class.getName());

    return new DomainEventKafkaPublisher(
        new KafkaProducer<>(properties),
        new Partitioner(4),
        "quickstart-events"
    );
  }
}
