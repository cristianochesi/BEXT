package bext;

public class TestBextQueues {

  
  public static void main(String[] args) {
    
    try {
    
      Bext.init();
      //BextConsumer.init();
      //BextConsumer.connect();
      BextPublisher.init();
      BextPublisher.connect();
      
      BextPublisher.sendCommand("STOP SESSION");
      System.out.println("Test done.");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
