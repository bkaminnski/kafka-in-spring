package com.hclc.kafkainspring.consumerassign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void consume(ConsumerRecord<String, String> record) {
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(record, failableMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
