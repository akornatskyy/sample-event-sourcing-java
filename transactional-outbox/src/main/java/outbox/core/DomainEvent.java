package outbox.core;

public final class DomainEvent<T> {
  public String type;
  public T data;
}
