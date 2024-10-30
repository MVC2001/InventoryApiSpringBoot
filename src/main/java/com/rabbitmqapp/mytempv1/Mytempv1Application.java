package com.rabbitmqapp.mytempv1;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class Mytempv1Application {

	public static void main(String[] args) {
		SpringApplication.run(Mytempv1Application.class, args);
	}

}
