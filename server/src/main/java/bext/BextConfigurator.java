package bext;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

/**
 * Extra environment variable used by this microservice.
 *
 * @author Mauro Dragoni (dragoni@fbk.eu).
 */
public interface BextConfigurator extends Config {

  // Basic settings
  String BEXT_USE_RABBIT = "USE_RABBIT";
  
  // GraphDB settings
  String BEXT_REPOSITORY_URL = "GRAPHDB_URL";
  String BEXT_REPOSITORY_ID = "REPOSITORY_ID";
  
  // User Profile settings
  String BEXT_USER_DEFAULT_PROFILES = "USER_DEFAULT_PROFILES";
  
  // RabbitMQ connection settings
  String BEXT_MQ_HOST = "RABBITMQ_HOST";
  String BEXT_MQ_VHOST = "RABBITMQ_VIRTUALHOST";
  String BEXT_MQ_USER = "RABBITMQ_USERNAME";
  String BEXT_MQ_PASSWORD = "RABBITMQ_PASSWORD";

  // RabbitMQ exchanges and routing keys
  String BEXT_MQ_EXCHANGE = "MQ_EXCHANGE";
  String BEXT_MQ_ROUTING_KEY_COMMANDS = "MQ_ROUTING_KEY_COMMANDS";
  String BEXT_MQ_ROUTING_KEY_DATA = "MQ_ROUTING_KEY_DATA";
  
  //MySQL connection settings
  String BEXT_DB_HOST = "DB_HOST";
  String BEXT_DB_NAME = "DB_NAMR";
  String BEXT_DB_USER = "DB_USERNAME";
  String BEXT_DB_PASSWORD = "DB_PASSWORD";
  
  
  /* Basic settings */
  @Key(BEXT_USE_RABBIT)
  @DefaultValue("1")
  String getBextUseRabbit();
  
  
  
  /* GraphDB settings */
  @Key(BEXT_REPOSITORY_URL)
  //@DefaultValue("http://localhost:7200")
  @DefaultValue("https://graphdb.fbk.eu")
  String getBextRepositoryUrl();

  
  @Key(BEXT_REPOSITORY_ID)
  //* Repositories uses the triplestore loader v1 (all-in-one HeLiS ontology) */
  //@DefaultValue("key-to-health")
  //@DefaultValue("salute-plus-development")
  //@DefaultValue("demo-helis")
  //@DefaultValue("test")
  //@DefaultValue("inmp")
  //@DefaultValue("helis")
  /* Repositories uses the triplestore loader v2 (HeLiS v1.50 - modularized ontology) */
  @DefaultValue("helis-puffbot")
  String getBextRepositoryId();

  
  /* User Profile settings */
  @Key(BEXT_USER_DEFAULT_PROFILES)
  @DefaultValue("SALUSPLUS")
  String getBextUserDefaultProfiles();
  
 
  
  
  /* RabbitMQ connection settings */
  @Key(BEXT_MQ_HOST)
  @DefaultValue("whale-01.rmq.cloudamqp.com")
  //@DefaultValue("mobsmq.fbk.eu")
  String getBextMqHost();

  @Key(BEXT_MQ_VHOST)
  //@DefaultValue("trec")
  @DefaultValue("iylqfsty")
  String getBextMqVHost();

  @Key(BEXT_MQ_USER)
  //@DefaultValue("developer")
  @DefaultValue("iylqfsty")
  String getBextMqUser();

  @Key(BEXT_MQ_PASSWORD)
  //@DefaultValue("rabbitTRECdeveloper!")
  @DefaultValue("DyfwPkqOFJiXM1_i9IFt5opO_2zgP0_1")
  String getBextMqPwd();

  
  /* MySQL connection settings */
  @Key(BEXT_DB_HOST)
  @DefaultValue("localhost")
  //@DefaultValue("mobsmq.fbk.eu")
  String getBextDBHost();

  @Key(BEXT_DB_NAME)
  //@DefaultValue("trec")
  @DefaultValue("bext")
  String getBextDBName();

  @Key(BEXT_DB_USER)
  //@DefaultValue("developer")
  @DefaultValue("root")
  String getBextDBUser();

  @Key(BEXT_DB_PASSWORD)
  //@DefaultValue("rabbitTRECdeveloper!")
  @DefaultValue("Root-2022")
  String getBextDBPwd();
  
  
  
  
  /* RabbitMQ exchanges and routing keys */
  @Key(BEXT_MQ_EXCHANGE)
  //@DefaultValue("salusplus")
  @DefaultValue("bext")
  String getBextMqExchange();
  
  @Key(BEXT_MQ_ROUTING_KEY_COMMANDS)
  @DefaultValue("bext.commands")
  String getBextMqRoutingKeyCommands();

  @Key(BEXT_MQ_ROUTING_KEY_DATA)
  @DefaultValue("bext.data")
  String getBextMqRoutingKeyData();



  /**
   * Factory for this configuration.
   *
   * @return Factory.
   */
  static BextConfigurator factory() {
    // Create a new configuration from ENV variables
    BextConfigurator config = ConfigFactory.create(BextConfigurator.class, System.getenv());

    return config;
  }

}
