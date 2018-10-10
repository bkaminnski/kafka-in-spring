package com.hclc.kafkainspring.failablemessages.consumed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumedRecord<K,V> {

    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final FailableMessage failableMessage;

    public ConsumedRecord(ConsumerRecord<K, V> consumerRecord, FailableMessage failableMessage) {
        this.consumerRecord = consumerRecord;
        this.failableMessage = failableMessage;
    }

    public ConsumerRecord<K, V> getConsumerRecord() {
        return consumerRecord;
    }

    public FailableMessage getFailableMessage() {
        return failableMessage;
    }
}
