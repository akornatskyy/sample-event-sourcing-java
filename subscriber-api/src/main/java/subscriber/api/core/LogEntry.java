package subscriber.api.core;

public class LogEntry {
  public int partition;
  public long offset;
  public String type;
  public String value;
}
