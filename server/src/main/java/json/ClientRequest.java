package json;

public class ClientRequest {
  
  private String idsessione;
  private long timestamp;
  private String command;
  
  
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
  public String getCommand() {
    return command;
  }
  public void setCommand(String command) {
    this.command = command;
  } 
}
