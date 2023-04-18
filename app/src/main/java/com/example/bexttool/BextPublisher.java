package com.example.bexttool;

import android.app.Activity;
import android.os.StrictMode;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class BextPublisher extends Thread {

    private Channel channel = null;
    private Connection connection = null;
    private String host;
    private String exchange;
    private String vhost;
    private String username;
    private String password;

    private String routingKey;
    //private static String routingKeyCommands;
    //private static Logger logger;

    private MainActivity parentActivity;


    public BextPublisher(MainActivity parent) {
        this.parentActivity = parent;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void run() {
        super.run();
        try {
            this.init();
            this.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        //logger = LoggerFactory.getLogger(BextPublisher.class);
        //logger.info("Initializing RabbitMQ.");

        try {

            //BextConfigurator prp = Bext.config;

            host = "whale-01.rmq.cloudamqp.com";
            exchange = "bext";
            vhost = "iylqfsty";
            username = "iylqfsty";
            password = "DyfwPkqOFJiXM1_i9IFt5opO_2zgP0_1";
            routingKey = "bext.data";

            //logger.info("Initializing Publisher on {}, {}, {} ", host, exchange, vhost);
            //logger.info("Initializing Publisher routing keys {}", routingKeyCommands);
            System.out.println("Initializing Publisher on " + host + " - " + exchange +
                               " - " + vhost);
            System.out.println("Initializing Publisher routing keys " + routingKey);

        } catch (Exception e) {
            //logger.info("Problems during queue publisher initialization.");
            //logger.info(Arrays.toString(e.getStackTrace()));
        }
    }



    public void connect() throws Exception {
        //logger.info("Connecting to the Queue Manager.");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setVirtualHost(vhost);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setRequestedHeartbeat(30);
        factory.setConnectionTimeout(30000);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();


        //logger.info("Connecting Publisher on {}, {}, {} ", host, exchange, vhost);
        //logger.info("Connecting Publisher routing keys {}", routingKeyCommands);
        System.out.println("Connecting Publisher on " + host + " - " + exchange +
                " - " + vhost);
        System.out.println("Connecting Publisher routing keys " + routingKey);

        //channel.exchangeDeclare(exchange, "topic", false);
    }



    public void closeConnection() {
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



    private void send(String routingKey, String msg, AMQP.BasicProperties theProps) {
        try {
            String message = msg;
            // channel.basicPublish(QUEUE_NAME,"", null, message.getBytes());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            //channel.basicPublish(exchange, routingKey, theProps, message.getBytes());
            channel.basicPublish(exchange, routingKey, theProps, bos.toByteArray());
            //logger.info(" [x] Sent '" + routingKey + "':'" + message + "'");
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
        } catch (Exception e) {
            //logger.info("Problems during message publishing.");
            //logger.info(Arrays.toString(e.getStackTrace()));
            e.getStackTrace();
        }
    }


    private void sendUser(String routingKey, String msg, AMQP.BasicProperties theProps) {
        try {
            String message = msg;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            //channel.basicPublish(exchange, routingKey, theProps, bos.toByteArray());
            //logger.info(" [x] Sent '" + routingKey + "':'" + message + "'");
        } catch (Exception e) {
            //logger.info("Problems during message publishing.");
            //logger.info(Arrays.toString(e.getStackTrace()));
        }
    }


    public void sendData(String msg) {
        System.out.println(" [x] Sending '" + routingKey + "':'" + msg + "'");
        send(routingKey, msg, null);
    }


    public void send(String routingKey, byte[] msg, AMQP.BasicProperties theProps) throws Exception {
        // channel.basicPublish(QUEUE_NAME,"", null, message.getBytes());
        channel.basicPublish(exchange, routingKey, theProps, msg);
        //logger.info(" [x] Sent '" + routingKey + "':'" + msg + "'");
        System.out.println(" [x] Sent '" + routingKey + "':'" + msg + "'");
    }


    /*
    public static void main(String[] args) {
        try {
            // ClientParameters.load();
            Bext.init();
            init();
            connect();
            //send("test.fbk.eu", "pippo", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
