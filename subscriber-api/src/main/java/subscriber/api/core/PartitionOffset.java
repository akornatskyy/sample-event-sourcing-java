package subscriber.api.core;

public final class PartitionOffset {
  private static final int SEPARATOR_CHAR = ',';
  private static final String SEPARATOR = Character.toString(SEPARATOR_CHAR);

  public int partition;
  public long offset;

  public String encode() {
    return partition + SEPARATOR + offset;
  }

  public static PartitionOffset decode(String s) {
    var i = s.indexOf(SEPARATOR_CHAR);
    var o = new PartitionOffset();
    o.partition = Integer.parseInt(s.substring(0, i));
    o.offset = Long.parseLong(s.substring(i + 1));
    return o;
  }
}
