package json;

import java.util.ArrayList;

public class SessionData {
  
  private String idsessione;
  private long timestamp;
  private ArrayList<DataPoint> data;
  
  public SessionData() {}
  
  public String getIdsessione() {
    return idsessione;
  }
  public void setIdsessione(String idsessione) {
    this.idsessione = idsessione;
  }
  public long getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  public ArrayList<DataPoint> getData() {
    return data;
  }
  public void setData(ArrayList<DataPoint> data) {
    this.data = data;
  }
  public void addDataPoint(DataPoint data) {
    this.data.add(data);
  }
}
