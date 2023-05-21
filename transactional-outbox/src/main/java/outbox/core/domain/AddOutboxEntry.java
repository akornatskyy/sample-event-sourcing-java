package outbox.core.domain;

public class AddOutboxEntry {
  public String key;
  public int partition;
  // TODO: add headers
  public byte[] data;
}
