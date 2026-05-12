package com.thinkfree.tfinder.common.config;

import com.thinkfree.tfinder.workspace.domain.MessageKey;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String INVITE_QUEUE_NAME = "email.invite";
    public static final String JOIN_QUEUE_NAME = "join.workspace";
    public static final String EXCHANGE_NAME = "tfinder.exchange";
    public static final String ROUTING_KEY = MessageKey.INVITE.getRoutingKey(); // 라우팅 키가 왜 필요함?? -> 이 키를 통해 어느 큐로 갈지 정해짐
    public static final String JOIN_KEY = MessageKey.JOIN_WORKSPACE.getRoutingKey(); // 라우팅 키가 왜 필요함?? -> 이 키를 통해 어느 큐로 갈지 정해짐

    @Bean
    public Queue inviteQueue() {
        return QueueBuilder.durable(INVITE_QUEUE_NAME).build();
    }

    @Bean
    public Queue joinQueue() {return QueueBuilder.durable(JOIN_QUEUE_NAME).build();}

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding inviteBinding(Queue inviteQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(inviteQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding joinBinding(Queue joinQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(joinQueue)
                .to(exchange)
                .with(JOIN_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }


}
