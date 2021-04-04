package com.mt.mall.port.adapter.messaging;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static com.mt.common.CommonConstant.EXCHANGE_ROLLBACK;

@Slf4j
@Component
public class SagaEventSubscriber {
    @Value("${mq.queueName}")
    private String appQueueName;
    @Value("${mq.routingKey}")
    private String appRoutingKey;

    @EventListener(ApplicationReadyEvent.class)
    public void initMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info("message received from mq for rollback");
            ApplicationServiceRegistry.getSkuApplicationService().rollback(message);
        };
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_ROLLBACK, "direct");
            String queueName = channel.queueDeclare(appQueueName, true, false, false, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_ROLLBACK, appRoutingKey);
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

}
