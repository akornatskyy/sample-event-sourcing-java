package playground;

public abstract class DomainEvent<T> {
  public String type;
  public T data;
}
