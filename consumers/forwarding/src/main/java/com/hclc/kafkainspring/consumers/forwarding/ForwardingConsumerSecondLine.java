package com.hclc.kafkainspring.consumers.forwarding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.monitoring.consumed.ConsumedRecord;
import com.hclc.kafkainspring.monitoring.failablemessages.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static java.lang.Thread.currentThread;

@Component
public class ForwardingConsumerSecondLine {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = {"forwardingConsumerSecondLineTopic"})
    public void consume(ConsumerRecord<String, String> record) {
        long consumedAtMonotonicNano = monotonicNowInNano();
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(consumedAtMonotonicNano, record, failableMessage, currentThread().getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
