package com.hclc.kafkainspring.monitoring.consumed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hclc.kafkainspring.monitoring.failablemessages.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumedRecord<K, V> {

    private final long consumedAtMonotonicNano;
    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final FailableMessage failableMessage;
    private final String containerThreadName;

    public ConsumedRecord(long consumedAtMonotonicNano, ConsumerRecord<K, V> consumerRecord, FailableMessage failableMessage, String containerThreadName) {
        this.consumedAtMonotonicNano = consumedAtMonotonicNano;
        this.consumerRecord = consumerRecord;
        this.failableMessage = failableMessage;
        this.containerThreadName = containerThreadName;
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

    public String getContainerThreadName() {
        return containerThreadName;
    }
}
