package com.eventbook.EventHub.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "eventhub.exchange";
    public static final String WAITLIST_QUEUE = "waitlist.ticket.released.queue";
    public static final String EMAIL_CONFIRMATION_QUEUE = "email.ticket.confirmation.queue";

    public static final String ROUTING_KEY_TICKET_RELEASED = "ticket.released";
    public static final String ROUTING_KEY_TICKET_PURCHASED = "ticket.purchased";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue waitlistQueue() {
        return new Queue(WAITLIST_QUEUE, true);
    }

    @Bean
    public Queue emailConfirmationQueue() {
        return new Queue(EMAIL_CONFIRMATION_QUEUE, true);
    }

    @Bean
    public Binding waitlistBinding(Queue waitlistQueue, TopicExchange exchange) {
        return BindingBuilder.bind(waitlistQueue).to(exchange).with(ROUTING_KEY_TICKET_RELEASED);
    }

    @Bean
    public Binding emailConfirmationBinding(Queue emailConfirmationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(emailConfirmationQueue).to(exchange).with(ROUTING_KEY_TICKET_PURCHASED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
