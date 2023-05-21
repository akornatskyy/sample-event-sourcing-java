package playground;

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer;
import com.amazonaws.services.schemaregistry.serializers.json.JsonDataWithSchema;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import playground.events.ActivateCustomerEvent;
import playground.events.UpdateCustomerEvent;
import software.amazon.awssdk.services.glue.model.Compatibility;
import software.amazon.awssdk.services.glue.model.DataFormat;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    property = "type")
abstract class BaseTypeMixIn {
}

public class UseCase {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  static {
    MAPPER.addMixIn(DomainEvent.class, BaseTypeMixIn.class);
    MAPPER.registerModule(
        new SimpleModule()
            .registerSubtypes(
                new NamedType(ActivateCustomerEvent.class, "ActivateCustomer"),
                new NamedType(UpdateCustomerEvent.class, "UpdateCustomer"))
    );
  }

  public static void publishWithGlueSchemaRegistry(String topic)
      throws IOException, URISyntaxException,
             ExecutionException, InterruptedException {
    try (var producer = new KafkaProducer<String, JsonDataWithSchema>(
        getProperties())) {

      var jsonSchema = Files.readString(
          Paths.get(UseCase.class.getClassLoader()
                        .getResource("schema.json")
                        .toURI()));
      var jsonPayload = Files.readString(
          Paths.get(UseCase.class.getClassLoader()
                        .getResource("sample-1.json")
                        .toURI()));
      var jsonSchemaWithData = JsonDataWithSchema
          .builder(jsonSchema, jsonPayload)
          .build();

      producer.send(new ProducerRecord<>(
              topic,
              0,
              "abc",
              jsonSchemaWithData))
          .get();
    }
  }

  public static void consumeWithGlueSchemaRegistry(String topic)
      throws JsonProcessingException {
    try (var consumer = new KafkaConsumer<String, JsonDataWithSchema>(
        getProperties())) {

      var partition = new TopicPartition(topic, 0);
      consumer.assign(List.of(partition));
      consumer.seek(partition, 0);

      var records = consumer.poll(Duration.ofSeconds(2));
      for (var r : records) {
        System.out.println(r.value().getPayload());

        var jsonNode = MAPPER.readTree(r.value().getPayload());
        var domainEvent = MAPPER.treeToValue(jsonNode, DomainEvent.class);
        switch (domainEvent.type) {
          case "ActivateCustomer" -> {
            var e = (ActivateCustomerEvent) domainEvent;
            System.out.println("customer to activate" + e.data.id);
          }
          case "UpdateCustomer" -> {
            var e = (UpdateCustomerEvent) domainEvent;
            System.out.println("customer to update " + e.data.id);
          }
        }
      }
    }
  }

  private static Properties getProperties() {
    var properties = new Properties();
    properties.setProperty(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());

    properties.setProperty(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        GlueSchemaRegistryKafkaSerializer.class.getName());
    properties.put(
        AWSSchemaRegistryConstants.DATA_FORMAT,
        DataFormat.JSON.name());
    properties.put(
        AWSSchemaRegistryConstants.AWS_REGION,
        "us-east-1");
    properties.put(
        AWSSchemaRegistryConstants.REGISTRY_NAME,
        "my-registry");
    properties.put(
        AWSSchemaRegistryConstants.SCHEMA_NAME,
        "my-schema");
    properties.put(
        AWSSchemaRegistryConstants.DESCRIPTION,
        "");
    properties.put(
        AWSSchemaRegistryConstants.COMPATIBILITY_SETTING,
        Compatibility.FORWARD_ALL);
    properties.put(
        AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING,
        true);

    properties.setProperty(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        GlueSchemaRegistryKafkaDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.GROUP_ID_CONFIG, "playground");
    properties.setProperty(
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return properties;
  }
}
