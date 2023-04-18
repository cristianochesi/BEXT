package bext;

import java.util.Arrays;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.DBLayer;

public class BextStarter implements ServletContextListener {
  
  private static Logger logger = LoggerFactory.getLogger(BextStarter.class);
  
  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    
    logger.info("BEXT Server is starting up!");
    
    try {
      try {
        Bext.init();
      } catch (Exception e) {
        logger.info("There was an issue during the initialization of the semantic environment. {}", e.getMessage(), e);
        logger.info(Arrays.toString(e.getStackTrace()));
      }
      
            
      BextConfigurator prp = Bext.config;
      
      
      try {
        DBLayer.connect(prp.getBextDBHost(), prp.getBextDBName(), prp.getBextDBUser(), prp.getBextDBPwd());
      } catch (Exception e) {
        logger.info("There was an issue during the initialization of database. {}", e.getMessage(), e);
        logger.info(Arrays.toString(e.getStackTrace()));
      }
      
      
      /* Connects to RabbitMQ only if it is used within the current instance. */
      if(prp.getBextUseRabbit().compareTo("1") == 0)
      {
        try {
          BextConsumer.init();
          BextConsumer.connect();
          BextPublisher.init();
          BextPublisher.connect();
          
        } catch (Exception e) {
          logger.info("Rabbit not connected. Queue requests and responses will not work. {}", e.getMessage(), e);
          logger.info(Arrays.toString(e.getStackTrace()));
        }
      }
      
    
    } catch (Exception e) {
      //throw new RuntimeException("Helis startup failed", e);
      logger.info("General problems during HeLiS start. {}", e.getMessage(), e);
      logger.info(Arrays.toString(e.getStackTrace()));
    }
  }

  
  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    System.out.println("BEXT Server is shutting down!");
    logger.info("BEXT Server is shutting down!");
    BextConfigurator prp = Bext.config;
    try {
      
      /* Closes the connections with RabbitMQ only if it was used by the current instance. */
      if(prp.getBextUseRabbit().compareTo("1") == 0)
      {
        BextConsumer.closeConnection();
        BextPublisher.closeConnection();
      }
      
            
      System.out.println("Closing connections...");
      logger.info("Closing connections...");
      System.out.println("Connections closed and repository shutdown.");
      logger.info("Connections closed and repository shutdown.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
