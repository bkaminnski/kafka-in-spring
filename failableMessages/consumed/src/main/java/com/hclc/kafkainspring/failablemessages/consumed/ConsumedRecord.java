package com.hclc.kafkainspring.failablemessages.consumed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumedRecord<K, V> {

    private final long consumedAtMonotonicNano;
    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final FailableMessage failableMessage;

    public ConsumedRecord(long consumedAtMonotonicNano, ConsumerRecord<K, V> consumerRecord, FailableMessage failableMessage) {
        this.consumedAtMonotonicNano = consumedAtMonotonicNano;
        this.consumerRecord = consumerRecord;
        this.failableMessage = failableMessage;
    }

    public long getConsumedAtMonotonicNano() {
        return consumedAtMonotonicNano;
    }

    public ConsumerRecord<K, V> getConsumerRecord() {
        return consumerRecord;
    }

    public FailableMessage getFailableMessage() {
        return failableMessage;
    }
}
