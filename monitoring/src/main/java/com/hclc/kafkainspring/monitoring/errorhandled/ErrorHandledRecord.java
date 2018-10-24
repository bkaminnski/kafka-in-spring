package com.hclc.kafkainspring.monitoring.errorhandled;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hclc.kafkainspring.monitoring.consumed.ConsumerRecordSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ErrorHandledRecord<K, V> {

    private final long errorHandledAtMonotonicNano;
    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final HandledException handledException;
    private final String containerThreadName;

    public ErrorHandledRecord(long errorHandledAtMonotonicNano, ConsumerRecord<K, V> consumerRecord, Exception exception, String containerThreadName) {
        this.errorHandledAtMonotonicNano = errorHandledAtMonotonicNano;
        this.consumerRecord = consumerRecord;
        this.handledException = new HandledException(exception);
        this.containerThreadName = containerThreadName;
    }

    public long getErrorHandledAtMonotonicNano() {
        return errorHandledAtMonotonicNano;
    }

    public ConsumerRecord<K, V> getConsumerRecord() {
        return consumerRecord;
    }

    public HandledException getHandledException() {
        return handledException;
    }

    public String getContainerThreadName() {
        return containerThreadName;
    }
}
