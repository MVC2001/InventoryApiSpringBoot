package com.rabbitmqapp.mytempv1.Config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate; // Use RabbitTemplate

    private static final String ROLE_QUEUE = "roleQueue";
    private static final String PERMISSION_QUEUE = "permissionQueue";
    private static final String USER_AUTH_QUEUE = "user_auth"; // Define the constant

    public void sendRoleMessage(String message) {
        rabbitTemplate.convertAndSend(ROLE_QUEUE, message);
    }

    public void sendUserAuthMessage(String message) {
        rabbitTemplate.convertAndSend(USER_AUTH_QUEUE, message); // Use RabbitTemplate
    }

    public void sendPermissionMessage(String message) {
        rabbitTemplate.convertAndSend(PERMISSION_QUEUE, message);
    }
}
