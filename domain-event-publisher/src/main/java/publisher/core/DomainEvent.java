package publisher.core;

public class DomainEvent<T> {
  public String type;
  public T data;
}
