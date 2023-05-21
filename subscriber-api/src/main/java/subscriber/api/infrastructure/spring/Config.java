package subscriber.api.infrastructure.spring;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import subscriber.api.core.LogConsumer;
import subscriber.api.core.SubscriberService;
import subscriber.api.infrastructure.kafka.LogKafkaConsumer;

@Configuration
class Config {
  private final Properties properties;

  Config() {
    properties = new Properties();
    properties.setProperty(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.GROUP_ID_CONFIG, "api");
    properties.setProperty(
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  }

  @Bean
  public SubscriberService subscriberService() {
    return new SubscriberService(this::domainEventConsumer);
  }

  private LogConsumer domainEventConsumer() {
    return new LogKafkaConsumer(
        new KafkaConsumer<>(properties));
  }
}
