package kz.project.reportsservice.producer;

import kz.project.reportsservice.data.dto.AmqpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Producer {
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing_key}")
    private String routingKey;

    private final RabbitTemplate template;

    public void sendMessage(AmqpDto message){
        template.convertAndSend(exchange,routingKey,message);
    }
}
