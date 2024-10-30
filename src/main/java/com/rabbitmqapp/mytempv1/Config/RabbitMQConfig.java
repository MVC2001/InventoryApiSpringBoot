package com.rabbitmqapp.mytempv1.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue roleQueue() {
        return new Queue("roleQueue", true); // Make the queue durable
    }

    @Bean
    public Queue permissionQueue() {
        return new Queue("permissionQueue", true); // Make the queue durable
    }

    @Bean
    public Queue userAuthQueue() {
        return new Queue("user_auth", true); // Make the queue durable
    }
}
