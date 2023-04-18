package bext;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import json.ClientRequest;
import json.DataPoint;
import json.SessionData;
import tools.DBLayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class BextConsumer {

  private static Channel channel = null;
  private static Connection connection = null;
  private static DefaultConsumer consumer;
  private static String host;
  private static String exchange;
  private static String vhost;
  private static String username;
  private static String password;
  private static String routingKey;
  private static Logger logger;

  private BextConsumer() {}

  
  public static void init() {

    logger = LoggerFactory.getLogger(BextConsumer.class);
    logger.info("Initializing RabbitMQ.");

    try {

      BextConfigurator prp = Bext.config;

      host = prp.getBextMqHost();
      exchange = prp.getBextMqExchange();
      vhost = prp.getBextMqVHost();
      username = prp.getBextMqUser();
      password = prp.getBextMqPwd();
      routingKey = prp.getBextMqRoutingKeyData();
      //routingKey = prp.getBextMqRoutingKeyCommands();

      logger.info("Initializing Consumer on {}, {}, {}, {}", host, exchange, vhost, routingKey);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void connect() throws Exception, IOException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setVirtualHost(vhost);
    factory.setUsername(username);
    factory.setPassword(password);
    connection = factory.newConnection();
    channel = connection.createChannel();

    logger.info("Connecting Consumer on {}, {}, {}, {}", host, exchange, vhost, routingKey);

    // channel.exchangeDeclare(Parameters._MQEXCHANGE, "topic");
    // String queueName = channel.queueDeclare().getQueue();
    //String queueName = channel.queueDeclare("bext-" + VC.config.getVirtualCoachRepositoryId(), true, false, false, null).getQueue();
    String queueName = channel.queueDeclare("bext-client", true, false, false, null).getQueue();

    channel.queueBind(queueName, exchange, routingKey);

    //System.out.println(" [*] Waiting for messages.");
    logger.info(" [*] Waiting for messages.");

    consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {

        // logger.info(body.toString());
        // String message = new String(body, "UTF-8");

        Object message = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(body);
        ObjectInputStream in = null;
        try {
          in = new ObjectInputStream(bis);
          message = in.readObject();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } finally {
          try {
            if (in != null) {
              in.close();
            }
          } catch (IOException ex) {
            // ignore close exception
          }
        }

        onMessage(message);
      }
    };

    channel.basicConsume(queueName, true, consumer);
  }

  
  
  public static void onMessage(Object message) {

    /**
     * Parse the Notification and run the related service
     */
    try {
      
      //System.out.println(message.toString());
      logger.info(message.toString());
      Gson gson = new Gson();
      SessionData p = gson.fromJson(message.toString(), SessionData.class);
      logger.info(gson.toJson(p));
      
      // INSERT INTO users_partners (uid,pid) VALUES (1,1) ON DUPLICATE KEY UPDATE uid=uid
      String query = "INSERT INTO session (id, timestamp) VALUES (" + p.getIdsessione() + ", " +
                     p.getTimestamp() + ") ON DUPLICATE KEY UPDATE idsession = idsession";
      DBLayer.SQL(query);
      
      for(DataPoint d : p.getData()) {
        query = "INSERT INTO data (idsession, timestamp, value) VALUES (" + p.getIdsessione() + ", " +
            d.getTimestamp() + ", " + d.getValue() + ") ON DUPLICATE KEY UPDATE idsession = idsession";
        DBLayer.SQL(query);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
  public static void closeConnection() {
    try {
      channel.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
