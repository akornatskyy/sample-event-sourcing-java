package playground;

public class Application {
  public static void main(String[] args) throws Exception {
    final String topic = "glue-events";
    UseCase.publishWithGlueSchemaRegistry(topic);
    UseCase.consumeWithGlueSchemaRegistry(topic);
  }
}