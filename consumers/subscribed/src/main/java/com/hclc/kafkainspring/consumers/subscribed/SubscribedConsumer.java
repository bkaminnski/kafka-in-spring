package com.hclc.kafkainspring.consumers.subscribed;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

public class SubscribedConsumer {

    @KafkaListener(topics = {"consumerSubscribeTopic"})
    public void listen(ConsumerRecord<?, ?> consumerRecord) throws Exception {
    }
}
