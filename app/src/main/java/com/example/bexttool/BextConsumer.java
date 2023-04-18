package com.example.bexttool;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import com.example.bexttool.json.ServerCommand;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.os.StrictMode;
import android.widget.Toast;

public class BextConsumer extends Thread {

    private Channel channel = null;
    private Connection connection = null;
    private DefaultConsumer consumer;
    private String host;
    private String exchange;
    private String vhost;
    private String username;
    private String password;
    private String routingKey;
    private MainActivity parentActivity;

    //private static Logger logger;

    public BextConsumer(MainActivity parent) {
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

        //logger = LoggerFactory.getLogger(BextConsumer.class);
        //logger.info("Initializing RabbitMQ.");
        System.out.println("Initializing RabbitMQ.");

        try {

            //BextConfigurator prp = Bext.config;

            host = "whale-01.rmq.cloudamqp.com";
            exchange = "bext";
            vhost = "iylqfsty";
            username = "iylqfsty";
            password = "DyfwPkqOFJiXM1_i9IFt5opO_2zgP0_1";
            routingKey = "bext.commands";

            //logger.info("Initializing Consumer on {}, {}, {}, {}", host, exchange, vhost, routingKey);
            System.out.println("Initializing Consumer on " + host + " - " + exchange + " - " + vhost + " - " + routingKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setVirtualHost(vhost);
        factory.setUsername(username);
        factory.setPassword(password);
        connection = factory.newConnection();
        channel = connection.createChannel();

        //logger.info("Connecting Consumer on {}, {}, {}, {}", host, exchange, vhost, routingKey);

        // channel.exchangeDeclare(Parameters._MQEXCHANGE, "topic");
        // String queueName = channel.queueDeclare().getQueue();
        //String queueName = channel.queueDeclare("bext-" + VC.config.getVirtualCoachRepositoryId(), true, false, false, null).getQueue();
        String queueName = channel.queueDeclare("bext-client", true, false, false, null).getQueue();

        channel.queueBind(queueName, exchange, routingKey);

        //logger.info(" [*] Waiting for messages.");
        System.out.println(" [*] Waiting for messages.");

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

    public void onMessage(Object message) {

        //logger.info(message.toString());
        System.out.println(message.toString());
        //logger.info(message.toString());
        Gson gson = new Gson();
        ServerCommand p = gson.fromJson(message.toString(), ServerCommand.class);
        System.out.println(gson.toJson(p));

        if(p.getCommand().compareTo("START") == 0) {
            System.out.println("Recording value ...");
            this.parentActivity.recordingFlag = true;
            Toast.makeText(this.parentActivity.getApplicationContext(), "Session start.", Toast.LENGTH_LONG).show();
        } else if(p.getCommand().compareTo("STOP") == 0) {
            System.out.println("Stopping recording ...");
            Toast.makeText(this.parentActivity.getApplicationContext(), "Session stop.", Toast.LENGTH_LONG).show();
            this.parentActivity.recordingFlag = false;
            this.parentActivity.sendDataToServer();
        }
        //Gson gson = new Gson();
        //ServerCommand p = gson.fromJson(message.toString(), ServerCommand.class);
        //logger.info(gson.toJson(p));
        //System.out.println(gson.toJson(p));

        /**
         * Parse the Notification and run the related service
         */
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
