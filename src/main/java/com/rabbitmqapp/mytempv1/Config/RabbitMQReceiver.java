package com.rabbitmqapp.mytempv1.Config;



import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiver {

    @RabbitListener(queues = "user_auth")
    public void receiveMessage(String message) {
        System.out.println("Received Message: " + message);
        // Process the message here
    }
}
