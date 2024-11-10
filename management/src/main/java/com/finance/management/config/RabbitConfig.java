package com.finance.management.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String INCOME_QUEUE = "incomeQueue";
    public static final String EXPENSE_QUEUE = "expenseQueue";
    public static final String USER_QUEUE = "user-queue";
    public static final String EXPENSE_RESPONSE_QUEUE = "expense-response-queue";

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }
    @Bean
    public Queue expenseResponseQueue() {
        return new Queue(EXPENSE_RESPONSE_QUEUE, true);
    }
    @Bean
    public Queue incomeQueue() {
        return new Queue(INCOME_QUEUE, true);
    }

    @Bean
    public Queue expenseQueue() {
        return new Queue(EXPENSE_QUEUE, true);
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
