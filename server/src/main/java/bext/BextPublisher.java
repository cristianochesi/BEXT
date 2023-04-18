package bext;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class BextPublisher {

  private static Channel channel = null;
  private static Connection connection = null;
  private static String host;
  private static String exchange;
  private static String vhost;
  private static String username;
  private static String password;
  
  //private static String routingKey;
  private static String routingKeyCommands;
  private static Logger logger;
  
  private BextPublisher() {}
  
  
  public static void init() {
    logger = LoggerFactory.getLogger(BextPublisher.class);
    logger.info("Initializing RabbitMQ.");
    
    try {
      
      BextConfigurator prp = Bext.config;
  
      host = prp.getBextMqHost();
      exchange = prp.getBextMqExchange();
      vhost = prp.getBextMqVHost();
      username = prp.getBextMqUser();
      password = prp.getBextMqPwd();
      routingKeyCommands = prp.getBextMqRoutingKeyCommands();
      
      logger.info("Initializing Publisher on {}, {}, {} ", host, exchange, vhost);
      logger.info("Initializing Publisher routing keys {}", routingKeyCommands);
      
    } catch (Exception e) {
      logger.info("Problems during queue publisher initialization.");
      logger.info(Arrays.toString(e.getStackTrace()));
    }
  }
  
  
  
  public static void connect() throws Exception {
    logger.info("Connecting to the Queue Manager.");
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setVirtualHost(vhost);
    factory.setUsername(username);
    factory.setPassword(password);
    factory.setRequestedHeartbeat(30);
    factory.setConnectionTimeout(30000);
    Connection connection = factory.newConnection();
    channel = connection.createChannel();
    
    
    logger.info("Connecting Publisher on {}, {}, {} ", host, exchange, vhost);
    logger.info("Connecting Publisher routing keys {}", routingKeyCommands);
    
    //channel.exchangeDeclare(exchange, "topic", false);
  }

  
  
  public static void closeConnection() {
    try {
      channel.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
  
  /*
  private static void send(String routingKey, String msg, AMQP.BasicProperties theProps) throws Exception {
    String message = msg;
    // channel.basicPublish(QUEUE_NAME,"", null, message.getBytes());
    channel.basicPublish(exchange, routingKey, theProps, message.getBytes());
    logger.info(" [x] Sent '" + routingKey + "':'" + message + "'");
  }
  */
  
  
  
  private static void send(String routingKey, String msg, AMQP.BasicProperties theProps) {
    try {
      String message = msg;
      // channel.basicPublish(QUEUE_NAME,"", null, message.getBytes());
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(message);
      //channel.basicPublish(exchange, routingKey, theProps, message.getBytes());
      channel.basicPublish(exchange, routingKey, theProps, bos.toByteArray());
      //System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
      logger.info(" [x] Sent '" + routingKey + "':'" + message + "'");
    } catch (Exception e) {
      logger.info("Problems during message publishing.");
      logger.info(Arrays.toString(e.getStackTrace()));
    }
  }
  
  
  private static void sendUser(String routingKey, String msg, AMQP.BasicProperties theProps) {
    try {
      String message = msg;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(message);
      //channel.basicPublish(exchange, routingKey, theProps, bos.toByteArray());
      logger.info(" [x] Sent '" + routingKey + "':'" + message + "'");
    } catch (Exception e) {
      logger.info("Problems during message publishing.");
      logger.info(Arrays.toString(e.getStackTrace()));
    }
  }

  
  public static void sendCommand(String msg) {
    send(routingKeyCommands, msg, null);
  }
  

  public static void send(String routingKey, byte[] msg, AMQP.BasicProperties theProps) throws Exception {
    // channel.basicPublish(QUEUE_NAME,"", null, message.getBytes());
    channel.basicPublish(exchange, routingKey, theProps, msg);
    logger.info(" [x] Sent '" + routingKey + "':'" + msg + "'");
  }

  
  
  public static void main(String[] args) {
    try {
      // ClientParameters.load();
      Bext.init();
      init();
      connect();
      send("bext.commands", "pippo", null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
