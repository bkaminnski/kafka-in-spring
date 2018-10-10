package com.hclc.kafkainspring.consumers.subscribed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.failablemessages.consumed.ConsumedRecord;
import com.hclc.kafkainspring.failablemessages.consumed.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SubscribedConsumer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = {"subscribedConsumerTopic"})
    public void consume(ConsumerRecord<String, String> record) throws Exception {
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(record, failableMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
