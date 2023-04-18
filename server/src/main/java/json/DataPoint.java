package json;

public class DataPoint {

  private long timestamp;
  private int value;
  
  public DataPoint(long t, int v) {
    this.timestamp = t;
    this.value = v;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
