package projector.core;

public abstract class DomainEvent<T> {
  public String type;
  public T data;
}
