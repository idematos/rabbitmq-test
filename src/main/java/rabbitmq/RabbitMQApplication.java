package rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class RabbitMQApplication {

    static final String JDBC_URL = System.getenv("JDBC_URL");
    static final String INSERTION_QUEUE_NAME = System.getenv("INSERTION_QUEUE");
    static final String VALIDATION_QUEUE_NAME = System.getenv("VALIDATION_QUEUE");
    static final String RESPONSE_EXCHANGE_NAME = System.getenv("RESPONSE_EXCHANGE");
    static final String RESPONSE_ROUTING_KEY = System.getenv("RESPONSE_ROUTING_KEY");
    static final String RABBITMQ_HOST = System.getenv("RABBITMQ_HOST");
    static final String RABBITMQ_VHOST = System.getenv("RABBITMQ_VHOST");
    static final String RABBITMQ_USERNAME = System.getenv("RABBITMQ_USERNAME");
    static final String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD");
    static final int RABBITMQ_PORT = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
    static final int NUMBER_OF_VALIDATION_CONSUMERS = Integer.parseInt(System.getenv("NUMBER_OF_VALIDATION_CONSUMERS"));

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(RABBITMQ_HOST);
        cachingConnectionFactory.setPort(RABBITMQ_PORT);
        cachingConnectionFactory.setVirtualHost(RABBITMQ_VHOST);
        cachingConnectionFactory.setUsername(RABBITMQ_USERNAME);
        cachingConnectionFactory.setPassword(RABBITMQ_PASSWORD);

        return cachingConnectionFactory;
    }

    @Bean
    Queue insertionQueue() {
        return new Queue(INSERTION_QUEUE_NAME, false);
    }

    @Bean
    SimpleMessageListenerContainer insertionContainer(ConnectionFactory connectionFactory, MessageListenerAdapter insertionListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(INSERTION_QUEUE_NAME);
        container.setMessageListener(insertionListenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter insertionListenerAdapter(InsertionConsumer insertionConsumer, MessageConverter jsonConverter) {
        return new MessageListenerAdapter(insertionConsumer, jsonConverter);
    }

    @Bean
    Queue validationQueue() {
        return new Queue(VALIDATION_QUEUE_NAME, false);
    }

    @Bean
    SimpleMessageListenerContainer validationContainer(ConnectionFactory connectionFactory, MessageListenerAdapter validationListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(VALIDATION_QUEUE_NAME);
        container.setMessageListener(validationListenerAdapter);
        container.setConcurrentConsumers(NUMBER_OF_VALIDATION_CONSUMERS);
        return container;
    }

    @Bean
    MessageListenerAdapter validationListenerAdapter(ValidationConsumer validationConsumer, MessageConverter jsonConverter) {
        return new MessageListenerAdapter(validationConsumer, jsonConverter);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(RESPONSE_EXCHANGE_NAME);
    }

    @Bean
    RabbitTemplate rabbitTemplate(MessageConverter jsonConverter, ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setRoutingKey(RESPONSE_ROUTING_KEY);
        rabbitTemplate.setExchange(RESPONSE_EXCHANGE_NAME);
        rabbitTemplate.setMessageConverter(jsonConverter);
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(JDBC_URL);
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource mysqlDataSource) {
        return new JdbcTemplate(mysqlDataSource);
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQApplication.class, args);
    }
}