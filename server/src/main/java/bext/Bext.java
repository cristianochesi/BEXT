package bext;

import org.aeonbits.owner.ConfigFactory;

public class Bext {

  private Bext() {}
  
  public static BextConfigurator config;
  
  public static void init() {
    
    try {
      config = ConfigFactory.create(BextConfigurator.class, System.getenv());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
